# System Patterns

## System Architecture
- MVVM (Model-View-ViewModel) architecture for clear separation of concerns.
- Repository pattern for data management and abstraction.

## Key Technical Decisions
- Use of Kotlin for development due to its modern features and Android compatibility.
- Room database for local storage to simplify data persistence.
- WorkManager for background tasks to ensure efficient resource usage.

## Design Patterns in Use
- Singleton pattern for shared components like the AudiobookRepository.
- Observer pattern for LiveData to manage UI updates reactively.

## Component Relationships
- **AudiobookApplication**: Initializes application-level components.
- **AudioPlaybackService**: Manages audio playback functionality.
- **LibraryScreen**: Displays the list of audiobooks.
- **PlayerScreen**: Handles playback controls and current audiobook display.
- **AudiobookRepository**: Central point for data operations, interacting with local storage and business logic.
