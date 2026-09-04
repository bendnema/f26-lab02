package edu.cmu.cs214.availability;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based tests for {@link AvailabilityCalculator}.
 *
 * <p>One example property is provided below: it checks that no returned free slot
 * overlaps a booking, and it passes. In Milestone 1 you add a stronger property
 * that pins down what "correct availability" actually means. See the lab handout.
 */
class AvailabilityProperties {

    private final AvailabilityCalculator calc = new AvailabilityCalculator();

    /** Provided example: every returned free slot is genuinely free (overlaps no booking). */
    @Property
    void freeSlotsNeverOverlapABooking(@ForAll("scenarios") Scenario s) {
        List<TimeInterval> free = calc.freeSlots(s.dayStart(), s.dayEnd(), s.bookings()); // generates a list of free slots
        for (TimeInterval slot : free) { // goes through each free slot
            for (TimeInterval booking : s.bookings()) { // goes through the bookings in each slot
                assertFalse(slot.overlaps(booking), // checks if the free slot overlaps with the booking, if it does, it fails the test
                    () -> "free slot " + slot + " overlaps booking " + booking); // if the free slot overlaps with the booking, it fails the test and prints a message
            }
        }
    }

    // --- Milestone 1: add your stronger property here ---

    /**
     * Every minute of the business day is accounted for exactly once: either it is covered
     * by a booking, or it is reported as free. Never both, and never neither.
     */
    @Property
    void everyMinuteIsEitherBookedOrReportedFree(@ForAll("scenarios") Scenario s) {
        List<TimeInterval> free = calc.freeSlots(s.dayStart(), s.dayEnd(), s.bookings());
        for (int minute = s.dayStart(); minute < s.dayEnd(); minute++) {
            final int m = minute;
            boolean booked = s.bookings().stream().anyMatch(b -> covers(b, m));
            boolean reportedFree = free.stream().anyMatch(f -> covers(f, m));
            assertTrue(booked != reportedFree,
                () -> "minute " + m + " is "
                    + (booked ? "booked but also reported free" : "neither booked nor reported free")
                    + "; day = [" + s.dayStart() + ", " + s.dayEnd() + ")"
                    + ", bookings = " + s.bookings()
                    + ", free = " + free);
        }
    }

    /** True when {@code interval} covers {@code minute}, using the half-open convention [start, end). */
    private static boolean covers(TimeInterval interval, int minute) {
        return interval.start() <= minute && minute < interval.end();
    }

    /** Generates a business day plus a list of bookings (possibly unsorted, overlapping, or outside hours). */
    @Provide
    Arbitrary<Scenario> scenarios() {
        Arbitrary<Integer> minutes = Arbitraries.integers().between(0, 1440);
        Arbitrary<TimeInterval> intervals = Combinators.combine(minutes, minutes)
            .as((a, b) -> new TimeInterval(Math.min(a, b), Math.max(a, b) + 1));
        Arbitrary<List<TimeInterval>> bookings = intervals.list().ofMaxSize(6);
        return Combinators.combine(minutes, minutes, bookings)
            .as((a, b, bk) -> new Scenario(Math.min(a, b), Math.max(a, b) + 1, bk));
    }

    record Scenario(int dayStart, int dayEnd, List<TimeInterval> bookings) {
    }
}
