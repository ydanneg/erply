# Erply
Erply Android Developer test task implementation

## Purpose
- Implementstion of a test task for Erply (Android developer) role
- Having a reference/template fully Kotlin/Croutines gradle-based project using the modern android architecture recommendations

The inspirstion comes from [Now in Android App](https://github.com/android/nowinandroid).

## Main challenges
- Use modern android architecture practices: [Guide to app architecture](https://developer.android.com/topic/architecture) 
- Use latest Compose Material 3 components and dynamic color scheme
- Use new Erply PIM API [PIM API](https://learn-api.erply.com/new-apis/pim-api)
- Implement syncronization (full/partial) with remote DB using Erply recommendation and the fact of hourly request limit: [Data Syncing](https://learn-api.erply.com/data-syncing)
- Use paging data from local DB using Room ORM ( potential large amount of data)
- Use advanced fast search using sqlite fts4 support using Room ORM
- Use well-typed Proto (protobuff) Datastore to store user preferences and session details
- Secure (encrypt) user credetials using device hardware backed keystore (HSM)
- Kotlin DSL for Erply API filtering

## Limitations
- Erply API discovery is not inomlemeted yet. See https://github.com/ydanneg/erply/issues/2
- Search is only available from inside group view, but anyway working globally. See https://github.com/ydanneg/erply/issues/1
- Erply PIM API does not provide endpoints to get deleted Product Groups. This limitation now makes fetching all groups every sync. Deleted groups still can be shown in UI.
- Erply Product Categories are not supported.
- Minum Android version 12

## Roadmap
See [Issues](https://github.com/ydanneg/erply/issues)
