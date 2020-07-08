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

function getDisplay() {
  fetch("/display").then(response => response.json()).then((list) => {
    // list is an arraylist containing strings, so we have to
    // reference its elements to create HTML content
    const contentListElement = document.getElementById("displayComponent");
    contentListElement.innerHTML = '';

    for (var i = 0; i < list.length; i++) {
      contentListElement.appendChild(
      createListElement(('Res: '+ list[i].resFileSize +'bytes')));
      contentListElement.appendChild(
      createListElement(('Java Code: '+ list[i].dexFileSize +'bytes')));
      contentListElement.appendChild(
      createListElement(('Libraries: '+ list[i].libraryFileSize +'bytes')));
      contentListElement.appendChild(
      createListElement(('Assets: '+ list[i].assetsFileSize +'bytes')));
      contentListElement.appendChild(
      createListElement(('Resources: '+ list[i].resourcesFileSize +'bytes')));
      contentListElement.appendChild(
        createListElement(('Miscellaneous: '+ list[i].miscFileSize +'bytes')));
        contentListElement.appendChild(
        createListElement(('Total '+ list[i].totalApkSize +'bytes')));
      }
    });
}

function displayFiles() {
  fetch('/retrieve_files').then(response => response.json()).then((apks) => {
    const apkListElement = document.getElementById('display-files');
    apks.forEach((apk) => {
      apkListElement.appendChild(createApkElement(apk));
    })
  });
}

function drawChart() {
    fetch("/display").then(response => response.json()).then((list) => {

        for ( var i = 0; i < list.length; i++) {
            var data = google.visualization.arrayToDataTable([
            ['Content', 'Size'],
            ['Res',  list[i].resFileSize ],
            ['Java Code',  list[i].dexFileSize],
            ['Libraries', list[i].libraryFileSize],
            ['Assets', list[i].assetsFileSize],
            ['Resources',list[i].resourcesFileSize ],
            ['Miscellaneous' , list[i].miscFileSize ]
        ]);
        }

        var options = {
          title: 'Apk Content',
          is3D: true,
        };

        var chart = new google.visualization.PieChart(document.getElementById('piechart_3d'));
        chart.draw(data, options);
     });

}

function deleteAPK(apk) {
  const params = new URLSearchParams();
  params.append('file_name', apk.name);
  fetch('/delete_file', {method: 'POST', body: params});
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
  exploreButtonElement.setAttribute('disabled', 'true');
  exploreButtonElement.addEventListener('click', () => {
    getDisplay();
    drawChart();
  });

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.className = 'btn btn-primary';
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.setAttribute('disabled', 'true');
  deleteButtonElement.addEventListener('click', () => {
    deleteAPK(apk);

    // Remove the apk from the DOM.
    apkElement.remove();
  });

  apkElement.appendChild(nameElement);
  apkElement.appendChild(exploreButtonElement);
  apkElement.appendChild(deleteButtonElement);

  return apkElement;
}