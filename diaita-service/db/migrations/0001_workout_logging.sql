-- Workout logging tables.
--
-- The service's runtime resources (src/main/resources) are not version controlled, so schema
-- changes live here and are applied to Supabase manually.
--
-- `exercises` remains the read-only exercise library backing POST /workouts/search. These two
-- tables store what a user actually performed.

create table if not exists public.workouts (
    id                uuid primary key,
    user_id           uuid        not null references auth.users (id) on delete cascade,
    name              text        not null check (length(btrim(name)) between 1 and 120),
    performed_at      timestamptz not null default now(),
    duration_seconds  integer     check (duration_seconds between 0 and 86400),
    notes             text,
    created_at        timestamptz not null default now(),
    updated_at        timestamptz not null default now()
);

create index if not exists workouts_user_performed_at_idx
    on public.workouts (user_id, performed_at desc);

create table if not exists public.workout_exercises (
    id                uuid primary key,
    workout_id        uuid    not null references public.workouts (id) on delete cascade,
    user_id           uuid    not null references auth.users (id) on delete cascade,
    exercise_id       integer references public.exercises (id) on delete set null,
    name              text    not null check (length(btrim(name)) > 0),
    category          text    not null check (category in ('lifting', 'cardio', 'mobility')),
    position          integer not null default 0 check (position >= 0),
    sets              integer check (sets between 0 and 100),
    reps              integer check (reps between 0 and 1000),
    weight_kg         numeric(7, 2) check (weight_kg between 0 and 2000),
    duration_seconds  integer check (duration_seconds between 0 and 86400),
    intensity         text,
    target            text,
    notes             text,
    created_at        timestamptz not null default now(),
    updated_at        timestamptz not null default now()
);

create index if not exists workout_exercises_workout_position_idx
    on public.workout_exercises (workout_id, position);

create index if not exists workout_exercises_user_idx
    on public.workout_exercises (user_id);

-- Keep updated_at honest on every write.
create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

drop trigger if exists workouts_set_updated_at on public.workouts;
create trigger workouts_set_updated_at
    before update on public.workouts
    for each row execute function public.set_updated_at();

drop trigger if exists workout_exercises_set_updated_at on public.workout_exercises;
create trigger workout_exercises_set_updated_at
    before update on public.workout_exercises
    for each row execute function public.set_updated_at();

-- Row level security: a user only ever sees their own logged work.
alter table public.workouts enable row level security;
alter table public.workout_exercises enable row level security;

drop policy if exists workouts_owner_access on public.workouts;
create policy workouts_owner_access on public.workouts
    for all
    using (auth.uid() = user_id)
    with check (auth.uid() = user_id);

drop policy if exists workout_exercises_owner_access on public.workout_exercises;
create policy workout_exercises_owner_access on public.workout_exercises
    for all
    using (auth.uid() = user_id)
    with check (auth.uid() = user_id);
