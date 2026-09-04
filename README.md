# Lab 2 Starter: Availability Calculator

A small reservation component. Given a room's bookings and the day's business hours,
`AvailabilityCalculator.freeSlots` computes when the room is free. It is the code you
work in for Lab 2.

It ships with a generated test suite that passes, and a property-based test harness
(jqwik) with one example property. Everything is green. Your job in Lab 2 is to decide
whether green actually means correct.

**Read `ARCHITECTURE.md` before the code.**

## Tools used

Claude Code (CLI), model Claude Opus 5.

## Build and test

```
mvn test
```

`mvn test` runs both files, the ordinary example-based tests (`AvailabilityCalculatorTest`)
and the property-based tests (`AvailabilityProperties`). A code-coverage report is written
to `target/site/jacoco/index.html`.

## Continuous integration

This repository has CI configured in `.github/workflows/ci.yml`. GitHub disables workflows on a
fresh fork, so enable them once on your fork (the handout shows where). After that, every
push runs `mvn test`. You will watch the gate go red when your new property finds the bug, then
green once you fix it.

## Where things are

- Component: `src/main/java/edu/cmu/cs214/availability/`
- Example-based tests: `src/test/java/edu/cmu/cs214/availability/AvailabilityCalculatorTest.java`
- Property-based tests: `src/test/java/edu/cmu/cs214/availability/AvailabilityProperties.java`
- Setup: `SETUP.md`

See the Lab 2 handout on the course page for the three milestones you show a TA.

## Milestone 3: why the old test suite missed the bug

1. Five tests `fullyBookedDayHasNoFreeSlots`, `bookingUntilEndOfDayLeavesTheMorningFree`,
`gapsBetweenBookingsAreReturned`, `unsortedBookingsAreHandled`, and
`overlappingBookingsAreMerged` in AvailbilityCalculatorTest.java end its final booking on day end, so theres no missing gap check, so the input never exposes the bug.  

2. On `returnedSlotsNeverOverlapABooking` the 6th although the final book is 600-660, the method dropped the rest of slot, and although it triggers the bug, the assertion only iterates on the returned list and if the returned slots overlap the booking. Since the dropped slot isn't in the the list it isn't able to check it and it comes back as correct

3. Finally the none of the test passes an empty booking list, which is an issue because if a whole day is free it will return a empty list with no times because it didn't througly check all the way to the end of day booking

Coverage didn't help because it measures whether a statment/method is executed and checked in the testing suit. Since no line could should up as red in the testing and every line ran, it came back as highly covered.
