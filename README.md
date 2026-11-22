# Greenhouse Controls

A Java greenhouse-control simulator that uses concurrent event execution, synchronized shared state, reflection-based event loading, serialization, and fault recovery.

## Features

- Runs greenhouse events using separate Java threads
- Supports suspend and resume behavior for active events
- Uses synchronized access to shared greenhouse state
- Dynamically loads event classes using Java reflection
- Reads event configuration from external files
- Handles simulated equipment failures and emergency shutdowns
- Saves and restores greenhouse state using Java serialization
- Supports repair workflows after system failures

## Technologies

- Java
- Concurrency and Multithreading
- Synchronization
- Reflection
- Serialization
- File I/O
- Exception Handling
- Object-Oriented Programming

## Purpose

This project was developed to practice concurrent Java programming, dynamic event creation, shared-state management, fault recovery, and persistent state handling.
