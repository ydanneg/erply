# Erply
Erply Android Developer test task implementation

## Purpose
- Implementation of a test task for Erply (Android developer) role
- Having a reference/template fully Kotlin/Coroutines gradle-based project using the modern android architecture recommendations

The inspiration comes from [Now in Android App](https://github.com/android/nowinandroid).

## Main challenges
- Use modern android architecture practices: [Guide to app architecture](https://developer.android.com/topic/architecture) 
- Use latest Compose Material 3 components and dynamic color scheme
- Use new Erply PIM API [PIM API](https://learn-api.erply.com/new-apis/pim-api)
- Implement syncronization (full/partial) with remote DB using Erply recommendation and the fact of hourly request limit: [Data Syncing](https://learn-api.erply.com/data-syncing)
- Use paging data from local DB using Room ORM ( potential large amount of data)
- Use advanced fast search using sqlite fts4 support using Room ORM
- Use well-typed Proto (protobuf) Datastore to store user preferences and session details
- Secure (encrypt) user credentials using device hardware backed keystore (HSM)
- Kotlin DSL for Erply API filtering

## Project structure
### Project root folder structure

```
.
├── api     # Erply API pure (not Android) 'java library' written in Kotlin
├── app     # Android application
├── gradle  # Gradle wrapper and Version Catalog
...
```

### Application module root folder structure

```
./app/src/main/kotlin/com/ydanneg/erply
├── data            # Data Layer (repositories)
├── database        # DB layer (database, entities, daos)
├── datastore       # Proto Datastores (user session, user prefereences)
├── di              # Dagger Hilt modules
├── domain          # Domain Layer (use cases)
├── model           # Application models
├── network         # Network data sources (Erply API data source)
├── security        # Encyption Manager
├── sync            # Sync realted logic (worker, sync manager)
└── ui              # UI Layer

```

#### UI Layer structure (composables + activity)
```
./app/src/main/kotlin/com/ydanneg/erply/ui
├── app                # Main Application composable
├── components         # Shared composable components
├── screens            # Composable screen components
│   ├── login          # Boarding flow: Login related composables and viewmodel
│   └── main           # Logged-In flow
│       ├── catalog    # List of Product Groups (Catalog) composables and viewmodel
│       ├── products   # List of Groups Products composables and viewmodel
│       └── settings   # Settings screen and viewmodel
├── theme              # Application Compose theme
└── util               # Some shared utilities

```

### Databse structure
```
../app/src/main/kotlin/com/ydanneg/erply/database
├── dao                # Dao
├── mappers            # Entity<->Domain Model mappers
├── model              # Entities
└── util               # Utilities, converters

```

## Architecture
This application follows [Official Android Architecure Guide](https://developer.android.com/topic/architecture)
Respected principals:
- [Separation of concerns](https://developer.android.com/topic/architecture#separation-of-concerns) design pattern.
- [Single source of truth](https://developer.android.com/topic/architecture#single-source-of-truth)
- [Unidirectional Data Flow](https://developer.android.com/topic/architecture#unidirectional-data-flow)
Layers:
- UI (MVVM), see [Ui Layer](https://developer.android.com/topic/architecture/ui-layer)
- Data, see [Data Layer](https://developer.android.com/topic/architecture/data-layer)
- Domain, see [Domain Layer](https://developer.android.com/topic/architecture/data-layer)

## Functionality
### Startup
LoginScreen is shown if user is not logged-in (UserSession.token is not available from datastore).
If login operation completes successfully and UserSession.isLoggedIn is true then ErplyApp renders MainScreen
MainScreen is shown if UserSession.token exists in a datastore.
### Main 
#### Catalog
Catalog is a root navigation route
Catalog screen shows the list of available Product groups downloaded (synced) from Erply PIM API. The list is shown immediatelly even if initial sync is not completed yet. List is bound to DB.
Clicking on a group item user is navigated to ProductsScreen by passing an argument 'groupId'.
Default sorting order is 'change, desc' for easy testing changes.
#### Product list
ProductsScreen is a child navigation route
Product list by groupId is fetched from DB as a PaginSource to support large amount of data.
ProductsScreen supports fast/advanced (fts4) full-text seearch that actually searches for all products, not just within a current group (see [#1](https://github.com/ydanneg/erply/issues/1)).
Default sorting order is 'change, desc' for easy testing changes.
#### Settings
SettingsScreen is a root navigation route
A simple Settings screen that only has Theme setting that allows to switch theme between Dark, Light and System Default modes
### Data Synchronization
Sync is currently triggered when MainScreenViewModel is initialized.
First login will trigger full sync that will download all clientCode related data from Erply PIM API.
Next sync will be a fast sync that downloads only changed data since last sync.
Worker job is used for synchronization. If Sync is failed it will be retried automatically.


## Limitations
- Erply API discovery is not implemented yet. See https://github.com/ydanneg/erply/issues/2
- Search is only available from inside group view, but anyway working globally. See https://github.com/ydanneg/erply/issues/1
- Erply PIM API does not provide endpoints to get deleted Product Groups. This limitation now makes fetching all groups every sync. Deleted groups still can be shown in UI.
- Erply Product Categories are not supported.
- Minimum Android version 12

## Roadmap
See [Issues](https://github.com/ydanneg/erply/issues)

