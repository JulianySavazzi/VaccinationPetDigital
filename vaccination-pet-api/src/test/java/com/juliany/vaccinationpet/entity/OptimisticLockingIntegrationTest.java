package com.juliany.vaccinationpet.entity;

import com.juliany.vaccinationpet.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OptimisticLockingIntegrationTest {

    private static final long WAIT_TIMEOUT_SECONDS = 10;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    void shouldIncrementVersionAfterSuccessfulUpdate() {
        Pet pet = createPersistedPet("Happy path");
        assertThat(pet.getVersion()).isZero();

        Pet updatedPet = transactionTemplate.execute(status -> {
            Pet managedPet = petRepository.findById(pet.getId()).orElseThrow();
            managedPet.setName("Updated pet");
            return petRepository.saveAndFlush(managedPet);
        });

        assertThat(updatedPet).isNotNull();
        assertThat(updatedPet.getVersion()).isEqualTo(1L);
        assertThat(updatedPet.getName()).isEqualTo("Updated pet");
    }

    @Test
    void shouldRejectConcurrentUpdateUsingStaleVersion() throws Exception {
        Pet pet = createPersistedPet("Concurrent path");
        Long petId = pet.getId();
        CountDownLatch bothTransactionsLoaded = new CountDownLatch(2);
        CountDownLatch firstTransactionCommitted = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<Long> firstUpdate = executor.submit(() -> transactionTemplate.execute(status -> {
                Pet managedPet = petRepository.findById(petId).orElseThrow();
                bothTransactionsLoaded.countDown();
                await(bothTransactionsLoaded);
                managedPet.setName("First update");
                return petRepository.saveAndFlush(managedPet).getVersion();
            }));

            Future<Void> staleUpdate = executor.submit(() -> transactionTemplate.execute(status -> {
                Pet managedPet = petRepository.findById(petId).orElseThrow();
                bothTransactionsLoaded.countDown();
                await(bothTransactionsLoaded);
                await(firstTransactionCommitted);
                managedPet.setName("Stale update");
                petRepository.saveAndFlush(managedPet);
                return null;
            }));

            assertThat(firstUpdate.get(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)).isEqualTo(1L);
            firstTransactionCommitted.countDown();

            assertThatThrownBy(() -> staleUpdate.get(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(OptimisticLockingFailureException.class);

            Pet persistedPet = transactionTemplate.execute(status ->
                    petRepository.findById(petId).orElseThrow()
            );
            assertThat(persistedPet).isNotNull();
            assertThat(persistedPet.getName()).isEqualTo("First update");
            assertThat(persistedPet.getVersion()).isEqualTo(1L);
        } finally {
            firstTransactionCommitted.countDown();
            executor.shutdownNow();
        }
    }

    private Pet createPersistedPet(String suffix) {
        return transactionTemplate.execute(status -> petRepository.saveAndFlush(new Pet(
                "Pet " + suffix,
                "DOG",
                Sex.FEMALE,
                LocalDate.of(2022, 5, 10),
                new Owner("Owner " + suffix, "owner@example.com")
        )));
    }

    private void await(CountDownLatch latch) {
        try {
            if (!latch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Timed out waiting for the concurrent transaction.");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while coordinating concurrent transactions.", exception);
        }
    }
}
