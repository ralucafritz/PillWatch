# PillWatch Documentation

PillWatch is an Android application designed to help users manage their medication intake. The application provides a comprehensive suite of features including medication reminders, drug interaction checks, user authentication, and more. 

The project is structured using the Model-View-ViewModel (MVVM) architecture pattern, ensuring a clean and maintainable codebase.

# Thesis

- [x] [Bachelor's Degree Thesis Romanian Documentation](/Bachelor's%20Degree%20Thesis%20-%20Romanian%20Documentation.pdf)
- [x] [Bachelor's Degree Thesis Romanian Presentation](/Bachelor's%20Degree%20Thesis%20-%20Romanian%20Presentation.pdf)


## Development Process

The development of PillWatch was carried out in a series of stages, each focusing on implementing or improving specific features of the application. Below is a chronological summary of the development process:

**Data Storage and Retrieval**: The Room database was implemented for efficient data storage and retrieval. Retrofit was utilized for making API calls to retrieve data from external sources. The database was successfully populated with the data received from the API.

**Data Integrity and Welcome Page**: A SHA check was added to verify if the file has been changed, ensuring data integrity. The Welcome Page was implemented, and support for both light and dark themes in the application was added.

**User Authentication and Project Architecture**: Firebase Auth was integrated for user authentication. Signup and Login activities were implemented. The project architecture was modified to follow the MVVM pattern. Extensions were added to facilitate global usage of commonly used functions. The navigation flow was improved to prevent potential looping between the Signup and Login pages.

**Persistent Authentication and Data Management**: Google Login/Signup was integrated. Persistent authentication state management was implemented. Password hashing was added for enhanced security. Coroutines were used for multi-threading. The structure of `MedsDataEntity` was refined. A utility object for `SharedPreferences` was created for accessing and managing `SharedPreferences` across the app.

**User Interface Improvements**: The font was updated for improved readability. The navigation flow between activities was improved for a smoother user experience. Data validation mechanisms for the `email`, `password`, and `confirm password` fields were improved.

**Loading Spinner Page and Animations**: The loading spinner page was designated as the starting fragment in the nav graph. Animations were implemented for seamless navigation between the loading spinner page and other pages.

**API Calls and Code Refactoring**: Initial implementation of meds interaction API call was done. `UserEntity` was updated with the addition of username and role fields. Code was refactored to improve organization and structure. User `ID` was saved to `SharedPreferences` for efficient user identification.

**Username Management and Data Validation**: Functionality to save the username to `SharedPreferences` was implemented. Input data validation for the username field was implemented to ensure the input is not null and has a minimum of 4 characters.

**Navigation Bar and Drawer Menu**: The bottom navigation bar with 3 options: `Home`, `Medication`, `Settings` was implemented. The drawer navigation menu was implemented with options for redirection to `Profile` or `Settings`, a `Logout` option, and a `Clean` option that deletes all data from the database.

**Medication Updates and Commenting**: A feature was implemented that allows users to check for medication updates. Comments were added for extension functions and the main activity.

**Login and Signup Logic Refactoring**: The login and signup logic was refactored to enhance separation of concerns and eliminate code duplication. Custom alarm icons


More informations on the development process can be found in the Pull Requests.

More information will be added in the near future.