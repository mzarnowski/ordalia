# High Way Cinema

Our client has a cinema in Wrocław, Poland. Currently, all movies scheduling is done by Pen and Paper on a big board where there is a plan for the given time for all the movies on display. Planner Jadwiga needs to schedule seans (i.e. a movie scheduled at a given time) the for best use of the space.

## Board overview

![Cinema - page 1 copy](https://user-images.githubusercontent.com/34231627/150541482-0b1e4a66-4298-4d3e-846f-c62ba1c8e37b.png)

## Dictionary

* **Show** - Movie which embed in schedule, meaning it has a point of time and a room picked.
* **Planner** - Person who works in the cinema and manages the week schedule of the shows.
* **Movie Catalog** - Place where the created movies are listed. There is a (rather small) chance a movie will not be displayed.
* **Cleaning slot** -  Time slot after each show necessary to clean the room.

## Domain requirements

We would like to help Jadwiga to do a better job with her weekly task of planning the show. Idea is to create virtual board, where she will be able to add shows to.

User Stories:
- Planner Jadwiga can schedule a show for the given movie at a particular time every day of the week from 8:00 to 22:00
- Any 2 scheduled movies can't be on same time and same room. Even the overlapping is forbidden.
- Every show needs to have a maintenance slot to clean up whole Room. Every room has different cleaning slot.
- Some movies require 3d glasses.
- Not every movie is equal e.g. Premier need to be after working hours, around 17:00-21:00.
- There is a possibility that a given room may not be available for a particular time slot or even day.


You task is to model the week planning of the shows by Jadwiga.

## Assumption
- Catalog of movies already exists (telling if it needs 3d glasses, how long the movie will take)

### Challenge notes

* Movie Catalog is not in scope of this challenge but some model will be required to fulfill given task.
* Consider concurrency modification. How to solve problem
  when two Jadwigas add different movies to the same time and the same room.
* If you have question to requirements just ask us.
* Using a real database or UI means losing precious time, so we encourage you to not do so.

#### What we care for:
- Solid domain model
- Quality of tests
- Clean code
- Proper module/context modeling

#### What we don’t care for:
- UI to be implemented
- Using database
- All the cases to be covered.

#### What we expect from solution:
- Treat it like a production code. Develop your software in the same way that you would for any code that is intended to be deployed to production.
- Would be good to describe decision you make so future developers won't be scratching the head about the reasoning.
- Test should be green
- Code should be on github repo.
