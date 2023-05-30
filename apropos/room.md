# Room Schedule
- A room operates during a continuous block of time decided by the cinema.
- room manages the movies it displays
- No two shows can overlap the same period of time
- the room must be cleaned after every show
- cleaning time can vary between the rooms
- a room maintains its availability

## Assumptions:
### the operating hours are a continuous block of time
### every day, room operates within the same hours
### the operating hours cannot be changed
#### Questions:
- what, if there is e.g. a spillage or malfunction?
### cleaning can be done after operating hours
### cleaning team will be dispatched automatically, and without fail
### the outside world doesn't need to know how long cleaning will take
Hence, the outgoing event will not contain this information, only the duration of the movie.
The room will still track this time, it will simply not expose it outside.
### we don't need to track which movie is scheduled at which time
Reason: there is no way to edit the schedule (yet).
Since we are emitting MovieScheduled events, it is easy to build state from those at a later time if needed.
### we don't plan to support overnight movies (i.e. also extending the operating hours)
### there are not many movies "in flight" at once
Even assuming we would try to schedule an entire month at once, when with a movie taking 30 minutes and we operate 24/7, this tops at 31 * 24 * 2 (= 1500) concurrent requests.

Usually, it would be much, much fewer requests (i.e. distribution heavily skewed toward lower volumes).
Hence, simple synchronisation when checking the daily availability of a room should suffice.
We can move to more complex solutions later, if needed.

In general, we should have fewer than 32 entries per day per room, so looking over them sequentially is also not a big cost.