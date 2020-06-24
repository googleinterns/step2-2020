TODO: Make a readme

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
