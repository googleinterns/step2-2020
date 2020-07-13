
# Project Vaderker

The goal of Project VADERKER is to enable developers to explore and analyze their APK files. 

<br/>

## Table of Contents
- [DATA](#Data-Analysis-Tool-for-Android-(DATA))
- [Developer Environment Setup](#Developer-Environment-Setup)
- [Code Style](#Code-Style)
- [License](#License)

## Data Analysis Tool for Android (DATA)

Through the Data Analysis Tool for Android (DATA), developers will be able to upload arbitrary android applications (APK), visually explore data compiled in the app, and analyze the components of the APK to scale the cost of each implementation.

<br/>

## Developer Environment Setup
 
### Install project dependencies on local development server

#### Add the Java 8 path to ~/.bashrc:

```sh
$ sudo update-java-alternatives -s java-1.8.0-openjdk-amd64
$ export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
$ export PATH=/usr/lib/jvm/java-8-openjdk-amd64/jre/bin:$PATH
```

#### Load the .bashrc changes and verify that Java 8 is being used:

```sh
$ % source ~/.bashrc
$ % java -version
$ openjdk version "1.8.0_232"
$ OpenJDK Runtime Environment (build 1.8.0_232-8u232-b09-1~deb9u1-b09)
$ OpenJDK 64-Bit Server VM (build 25.232-b09, mixed mode)
```

#### Install mvn and npm:

```sh
$ sudo apt-get update
$ sudo apt-get install maven -y
$ sudo apt-get install nodejs npm -y
$ sudo npm install npm -g
$ sudo npm install npm -g @angular/cli
```

#### Get the code, then run it from the capstone directory:

```sh
$ git clone https://github.com/googleinterns/step2-2020
$ cd step2-2020/capstone (in the directory you cloned the repo)
$ mvn package appengine:run
```

#### Now you can open the app in your browser at: http://localhost:8080/

<br/>

---

<br/>

### Additional Instructions for using DATA before first version launch 

> Due to the ongoing development of the project, running this project on local computer will require some additional steps

### Initial things to note

After running the webapp and previewing through locahost, you can migrate to the ***Explore*** page through the ***Explore*** button on the home page.

While on the ***Explore*** page, you will be able to view the list of apks that have been uploaded by the team to the [Cloud Storage](https://pantheon.corp.google.com/storage/browser/vaderker-uploadedstoragebucket/apks;tab=objects?forceOnBucketsSortingFiltering=false&project=step-2020-team-2) directory specifically for the project.  
  
  > **Please note:** you will not have access to the directory until granted _read_ and _write_ access by the team.
  This means that you will not be able to carry out various functions that **DATA** provides since you are in developer mode. 
  However, you can view and use functions **DATA** provides on the [Live Server](https://step-2020-team-2.uc.r.appspot.com/#/home).

[Google Cloud Shell Console](https://cloud.google.com/) is the preferred developer environment due to certain reasons. 
 > - It allows easy connections with other Google Cloud resources such as Cloud Storage and Datastore
 > - The Upload and Unzip functions will not work unless running on Google CloudShell Platform 

<br/>

---

<br/>

### Further things to note after granted permissions


#### Info about Google Cloud Platforms


> This project works with Google Cloud Storage and Cloud Datastore.
Cloud Storage is the destination for uploads and is universal to the entire project.
Cloud Datastore store features retrieved from the APK. It is universal for all developers on the live server, but specific for every developer.

After access to Cloud Storage has been granted by the team, you will be able to upload apks with moderate sizes (est. 40mb) to Cloud Storage
which will be automatically unzipped to your local Datastore by **DATA** and would appear as part of the apks list on the **EXPLORE** page.

However, if you would also like to explore the apks uploaded by the team, you would have to unzip that in your local Datastore through the process below (This is entirely optional).
  
  > - To unzip already provided apks and store in your local Datastore, you will need to download the apk you want from the [Cloud Storage Directory](https://pantheon.corp.google.com/storage/browser/vaderker-uploadedstoragebucket/apks;tab=objects?forceOnBucketsSortingFiltering=false&project=step-2020-team-2) 
  > - Once downloaded, delete the apk from your local webapp through the **Delete** button and re-upload the apk. This will ensure it is unzipped and stored in your local Datastore so that you can explore it.
  > - To access your local Datastore, migrate to : http://localhost:8080/_ah/admin to ensure it has been stored. 


```
If you ever come across the error, "Data table not defined" after clicking explore for any of the apks, 
it simply means the apk hasn't been unzipped and stored in your local Datastore.
```

After completion of all these steps, you will be able to contribute to the development of **DATA** and test your improvements locally.

<br/>

---

<br/>

### Code Style

This project uses code based on Google Style Guides which can be found at [Google Style Guides](https://google.github.io/styleguide/)

This project also uses `clang-format` to maintain style across Java and JavaScript files. This can be installed using

```sh
$ sudo apt install clang-format
```

A git-hook can be enabled to automatically run the formatter on commits. Enable the auto-formatting with:

```sh
$ git config core.hooksPath .githooks
```

## 

### License 

This project is owned by Google LLC, but not an official Google LLC product.