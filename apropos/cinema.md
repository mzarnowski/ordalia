# Cinema
Maintains a set of cinema-level policies, like when is the specific screening allowed.

## Assumptions
### A policy never changes
### the operating hours are a continuous block of time
### It is okay, if sometimes the planner sees an error when trying to schedule a near-premiere movie
This might happen, because the screening archive might not yet have picked the ScreeningScheduled event.
It is okay, because a given movie has premiere only once, so it should happen relatively seldom.

To prevent this issue, we would have to extend the consistency guarantees to encapsulate both a room and 
screening archive at the same time.

### the operating hours cannot be changed
### the operating hours are the same every day of the week
### cleaning rooms can be done after operating hours
### we don't plan to support overnight movies (which would depend on changing the operating hours)
