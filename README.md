
# Project Vaderker
## Data Analysis Tool for Android (DATA)

The goal of Project VADERKER is to enable developers to explore and analyze their APK files. 

Through the Data Analysis Tool for Android (DATA), developers will be able to upload arbitrary android applications (APK), visually explore data compiled in the app, and analyze the components of the APK to scale the cost of each implementation.

## Developer Environment Set up

This project uses `clang-format` to maintain style across Java and JavaScript
files. This can be installed using

```
sudo apt install clang-format
```

A git-hook can be enabled to automatically run the formatter on commits. Enable
the auto-formatting with:

```
git config core.hooksPath .githooks
```
