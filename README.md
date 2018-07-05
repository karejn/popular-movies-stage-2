Popular Movies Stage 2 (Project 3) - Udacity Android Developer Nanodegree

## Description
Made as part of Udacity's [Android Developer Nanodegree Program](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801).
This app:
- Presents the user with a grid arrangement of movie posters upon launch.
- Allows the user to change sort order via a setting:

    - The sort order can be by most popular, highest-rated, or favorites.

- Allows the user to tap on a movie poster and transition to a details screen with additional information such as:

    - original title
    - movie poster image thumbnail
    - a plot synopsis (called overview in the api)
    - user rating (called vote_average in the api)
    - release date

- Allows users to view and play trailers ( either in the youtube app or a web browser).

- Allows users to read reviews of a selected movie.

- Allows users to mark a movie as a favorite in the details view by tapping a button(star).

- Allows to create a database to store the names and ids of the user's favorite movies (and optionally, the rest of the information needed to display their favorites collection while offline).



## API Key
The movie information uses [The Movie Database (TMDb)](https://www.themoviedb.org/documentation/api) API.
To make your app work, you have to enter your own API key into `gradle.properties` file.

```gradle.properties
MOVIE_DB_API_KEY="Api Key"
