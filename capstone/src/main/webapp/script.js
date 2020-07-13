// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.



// This function tells angular what buttons should be displayed
// or hidden depending on the login status of the user. 
async function logInStatus() {

  const response = await fetch("/signin", {method: 'POST'});
  const isLoggedin  = await response.text();

  if (isLoggedin.trim() == "true") {

    document.getElementById('login').hidden = true;
    document.getElementById('logout').hidden = false;
    document.getElementById('upload').hidden = false;

  } else {
    
    document.getElementById('logout').hidden = true;
    document.getElementById('upload').hidden = true;
    document.getElementById('login').hidden = false;
  }

}

// showFileStatistics retrieves information from the
// FileDisplayServlet for drawChart to display the content graph and
// getDisplay to show the statistics of the file

async function showFileStatistics(filename, time) {
  const params = new URLSearchParams();
  params.append('apk_name', filename);
  params.append('timeStamp', time);

  const response = await fetch("/display", {method: 'POST', body: params});
  const fileStatistics = await response.json();

  getDisplay(fileStatistics);
  drawChart(fileStatistics);
}

// getDisplay makes a user aware of how much space each component
// of the APK consumes.

function getDisplay(list) {
  // list is an arraylist containing strings, so we have to
  // reference its elements to create HTML content
  const contentListElement = document.getElementById("displayComponent");
  contentListElement.innerHTML = '';

  for (var i = 0; i < list.length; i++) {
    contentListElement.appendChild(
    createListElement(('Res: '+ list[i].resFileSize +' bytes')));
    contentListElement.appendChild(
    createListElement(('Java Code: '+ list[i].dexFileSize +' bytes')));
    contentListElement.appendChild(
    createListElement(('Libraries: '+ list[i].libraryFileSize +' bytes')));
    contentListElement.appendChild(
    createListElement(('Assets: '+ list[i].assetsFileSize +' bytes')));
    contentListElement.appendChild(
    createListElement(('Resources: '+ list[i].resourcesFileSize +' bytes')));
    contentListElement.appendChild(
      createListElement(('Miscellaneous: '+ list[i].miscFileSize +' bytes')));
      contentListElement.appendChild(
      createListElement(('Total: '+ list[i].totalApkSize +' bytes')));
  }
}

function drawChart(list) {

  for ( var i = 0; i < list.length; i++) {
      var data = google.visualization.arrayToDataTable([
      ['Content', 'Size'],
      ['Res',  list[i].resFileSize[0] ],
      ['Java Code',  list[i].dexFileSize[0]],
      ['Libraries', list[i].libraryFileSize[0]],
      ['Assets', list[i].assetsFileSize[0]],
      ['Resources',list[i].resourcesFileSize[0] ],
      ['Miscellaneous' , list[i].miscFileSize[0] ]
  ]);
  }

  var options = {
    title: 'Apk Content',
    is3D: true,
  };

  var chart = new google.visualization.PieChart(document.getElementById('piechart_3d'));
  chart.draw(data, options);

}

function displayFiles() {
  fetch('/retrieve_files').then(response => response.json()).then((apks) => {
    const apkListElement = document.getElementById('display-files');
    apks.forEach((apk) => {
      apkListElement.appendChild(createApkElement(apk));
    })
  });
}

function deleteAPK(fileName, fileOwnership) {
  const params = new URLSearchParams();
  params.append('file_name', fileName);
  params.append('ownership', fileOwnership);
  fetch('/delete_file', {method: 'POST', body: params});
}


// This function hides the privacy option
// for file upload. It only shows it when a file
// has been selected for upload.
function fileVisibility() {
  var file = document.getElementById('file').value;
  if (file.length != 0) {
    document.getElementById('private').hidden = false;
    document.getElementById('privacy').hidden = false;
    document.getElementById('public').hidden = false;
    document.getElementById('privacies').hidden = false;
  } else {
    document.getElementById('private').hidden = true;
    document.getElementById('privacy').hidden = true;
    document.getElementById('public').hidden = true;
    document.getElementById('privacies').hidden = true;
  }
}

function createListElement(text) {
  const liElement = document.createElement('ul');
  liElement.innerText = text;
  return liElement;
}

function createApkElement(apk) {
  const apkElement = document.createElement('li');
  apkElement.className = 'binary_file';

  const nameElement = document.createElement('span');
  nameElement.innerText = apk.name;

  const exploreButtonElement = document.createElement('button');
  exploreButtonElement.className = 'btn btn-primary';
  exploreButtonElement.innerText = 'Explore';
  exploreButtonElement.addEventListener('click', () => {
    showFileStatistics(apk.name, apk.time);
  });

  apkElement.appendChild(nameElement);
  apkElement.appendChild(exploreButtonElement);

  if (apk.isOwner.trim() == "true" || apk.isOwner.trim() == "true1") {

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.className = 'btn btn-primary';
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.addEventListener('click', () => {
      deleteAPK(apk.name, apk.isOwner.trim());

      // Remove the apk from the DOM.
      apkElement.remove();
    });

    apkElement.appendChild(deleteButtonElement);
  }

  return apkElement;
}