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
      createListElement(('Dex: '+ list[i].dexFileSize +'bytes')));
      contentListElement.appendChild(
      createListElement(('Lib: '+ list[i].libraryFileSize +'bytes')));
      contentListElement.appendChild(
      createListElement(('Ass: '+ list[i].assetsFileSize +'bytes')));
      contentListElement.appendChild(
      createListElement(('Rsc: '+ list[i].resourcesFileSize +'bytes')));
      contentListElement.appendChild(
      createListElement(('Msc: '+ list[i].miscFileSize +'bytes')));
      contentListElement.appendChild(
      createListElement(('Total '+ list[i].totalApkSize +'bytes')));
    }
  });
}

function createListElement(text) {
    const liElement = document.createElement('ul');
    liElement.innerText = text;
    return liElement;
}

function drawChart() {
    fetch("/display").then(response => response.json()).then((list) => {

        for ( var i = 0; i < list.length; i++) {
            var data = google.visualization.arrayToDataTable([
            ['Content', 'Size'],
            ['Res',  list[i].resFileSize ],
            ['Dex',  list[i].dexFileSize],
            ['Libraries', list[i].libraryFileSize],
            ['assets', list[i].assetsFileSize],
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