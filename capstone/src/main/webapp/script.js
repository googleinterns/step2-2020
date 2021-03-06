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

async function loadCorrectPageDOM() {

  logInStatus();

  if (window.location.hash === '#/explore') {

    document.getElementById('searchBar').hidden = false;
    fileDisplayAndSearchQuery();

  } else { document.getElementById('searchBar').hidden = true; }

}

var apkName;
function getFreqData(list){
    var res, javaCode, libraries, assets,resources, miscellaneous,total, freqData;
    var uncomRes, uncomJavaCode, uncomLibraries, uncomAssets,uncomResources, uncomMiscellaneous;

    for (var i = 0; i < list.length; i++) {
        res = list[i].resFileSize[1];
        uncomRes = list[i].resFileSize[0];
        javaCode = list[i].dexFileSize[1];
        uncomJavaCode= list[i].dexFileSize[0];
        libraries = list[i].libraryFileSize[1];
        uncomLibraries = list[i].libraryFileSize[0];
        assets =  list[i].assetsFileSize[1];
        uncomAssets = list[i].assetsFileSize[0];
        resources =  list[i].resourcesFileSize[1];
        uncomResources =  list[i].resourcesFileSize[0];
        miscellaneous=  list[i].miscFileSize[1];
        uncomMiscellaneous = list[i].miscFileSize[0];
        total =  list[i].totalApkSize;
    }

    freqData=[
        {fileType:'Res',freq:{Compressed:res, Uncompressed:uncomRes, Space_saved_after_Compression:(uncomRes - res)}},
        {fileType:'Java Code',freq:{Compressed:javaCode, Uncompressed:uncomJavaCode, Space_saved_after_Compression:(uncomJavaCode - javaCode)}},
        {fileType:'Resources',freq:{Compressed:resources, Uncompressed:uncomResources, Space_saved_after_Compression:(uncomResources - resources )}},
        {fileType:'Miscellaneous',freq:{Compressed:miscellaneous, Uncompressed:uncomMiscellaneous, Space_saved_after_Compression:(uncomMiscellaneous - miscellaneous)}},
        {fileType:'Assets',freq:{Compressed:assets, Uncompressed:uncomAssets, Space_saved_after_Compression:(uncomAssets - assets)}}
    ]; 
    return freqData;
}


function changeChart(fileStatistics, result){ 
    if ( result == 1){
        document.getElementById("displayChart").style.display = 'none';
        document.getElementById("displayComponent").style.display= 'none';
        document.getElementById("display").style.display = 'block';
        d3.select("#display").selectAll("svg").remove();
        d3.select("#display").selectAll("table").remove();
        //d3.select("#display").text(apkName);
        dashboard(getFreqData(fileStatistics));     
    }
    else if (result == 2){
        document.getElementById("display").style.display = 'none';
        document.getElementById("displayComponent").style.display= 'none';
        document.getElementById("displayChart").style.display = 'block'; 
        drawChart(fileStatistics);
    }
    else if (result == 3) {
        document.getElementById("displayChart").style.display = 'none';
        document.getElementById("display").style.display = 'none';
        document.getElementById("displayComponent").style.display= 'block';
        getDisplay(fileStatistics);
    }
}

// showFileStatistics retrieves information from the
// FileDisplayServlet for drawChart to display the content graph and
// getDisplay to show the statistics of the file
async function showFileStatistics(filename, time, result) {

    const params = new URLSearchParams();
    params.append('apkName', filename);
    params.append('timeStamp', time);

    apkName = filename;
    const response = await fetch("/display", {method: 'POST', body: params});
    const fileStatistics = await response.json();
    
    changeChart(fileStatistics,result);
    exploreParser(filename, time);
}

function dashboard(fData){

    var id = document.getElementById('display');
    d3.select("#display").text(apkName);
    var barColor = 'steelblue';
    function segColor(c){ return {Compressed:"#807dba", Uncompressed:"#e08214",Space_saved_after_Compression:"#41ab5d"}[c]; }
    
    // compute total for each state.
    fData.forEach(function(d){d.total=(d.freq.Uncompressed)});
    
    // function to handle histogram.
    function histoGram(fD){
        var hG={},    hGDim = {t: 60, r: 0, b: 30, l: 0};
        hGDim.w = 500 - hGDim.l - hGDim.r, 
        hGDim.h = 300 - hGDim.t - hGDim.b;
            
        //create svg for histogram.
        var hGsvg = d3.select(id).append("svg")
            .attr("width", hGDim.w + hGDim.l + hGDim.r)
            .attr("height", hGDim.h + hGDim.t + hGDim.b).append("g")
            .attr("transform", "translate(" + hGDim.l + "," + hGDim.t + ")");
 
        // create function for x-axis mapping.
        var x = d3.scale.ordinal().rangeRoundBands([0, hGDim.w], 0.1)
                .domain(fD.map(function(d) { return d[0]; }));
 
        // Add x-axis to the histogram svg.
        hGsvg.append("g").attr("class", "x axis")
            .attr("transform", "translate(0," + hGDim.h + ")")
            .call(d3.svg.axis().scale(x).orient("bottom"));
 
        // Create function for y-axis map.
        var y = d3.scale.linear().range([hGDim.h, 0])
                .domain([0, d3.max(fD, function(d) { return d[1]; })]);
 
        // Create bars for histogram to contain rectangles and freq labels.
        var bars = hGsvg.selectAll(".bar").data(fD).enter()
                .append("g").attr("class", "bar");
        
        //create the rectangles.
        bars.append("rect")
            .attr("x", function(d) { return x(d[0]); })
            .attr("y", function(d) { return y(d[1]); })
            .attr("width", x.rangeBand())
            .attr("height", function(d) { return hGDim.h - y(d[1]); })
            .attr('fill',barColor)
            .on("mouseover",mouseover)// mouseover is defined below.
            .on("mouseout",mouseout);// mouseout is defined below.
            
        //Create the frequency labels above the rectangles.
        bars.append("text").text(function(d){ return d3.format(",")(d[1])})
            .attr("x", function(d) { return x(d[0])+x.rangeBand()/2; })
            .attr("y", function(d) { return y(d[1])-5; })
            .attr("text-anchor", "middle");
        
        function mouseover(d){  // utility function to be called on mouseover.
            // filter for selected state.
            var st = fData.filter(function(s){ return s.fileType == d[0];})[0],
                nD = d3.keys(st.freq).map(function(s){ return {type:s, freq:st.freq[s]};});
               
            // call update functions of pie-chart and legend.    
            pC.update(nD);
            leg.update(nD);
        }
        
        function mouseout(d){    // utility function to be called on mouseout.
            // reset the pie-chart and legend.    
            pC.update(tF);
            leg.update(tF);
        }
        
        // create function to update the bars. This will be used by pie-chart.
        hG.update = function(nD, color){
            // update the domain of the y-axis map to reflect change in frequencies.
            y.domain([0, d3.max(nD, function(d) { return d[1]; })]);
            
            // Attach the new data to the bars.
            var bars = hGsvg.selectAll(".bar").data(nD);
            
            // transition the height and color of rectangles.
            bars.select("rect").transition().duration(500)
                .attr("y", function(d) {return y(d[1]); })
                .attr("height", function(d) { return hGDim.h - y(d[1]); })
                .attr("fill", color);
 
            // transition the frequency labels location and change value.
            bars.select("text").transition().duration(500)
                .text(function(d){ return d3.format(",")(d[1])})
                .attr("y", function(d) {return y(d[1])-5; });            
        }        
        return hG;
    }
    
    // function to handle pieChart.
    function pieChart(pD){
        var pC ={},    pieDim ={w:250, h: 250};
        pieDim.r = Math.min(pieDim.w, pieDim.h) / 2;
                
        // create svg for pie chart.
        var piesvg = d3.select(id).append("svg")
            .attr("width", pieDim.w).attr("height", pieDim.h).append("g")
            .attr("transform", "translate("+pieDim.w/2+","+pieDim.h/2+")");
        
        // create function to draw the arcs of the pie slices.
        var arc = d3.svg.arc().outerRadius(pieDim.r - 10).innerRadius(0);
 
        // create a function to compute the pie slice angles.
        var pie = d3.layout.pie().sort(null).value(function(d) { return d.freq; });
 
        // Draw the pie slices.
        piesvg.selectAll("path").data(pie(pD)).enter().append("path").attr("d", arc)
            .each(function(d) { this._current = d; })
            .style("fill", function(d) { return segColor(d.data.type); })
            .on("mouseover",mouseover).on("mouseout",mouseout);
 
        // create function to update pie-chart. This will be used by histogram.
        pC.update = function(nD){
            piesvg.selectAll("path").data(pie(nD)).transition().duration(500)
                .attrTween("d", arcTween);
        }        
        // Utility function to be called on mouseover a pie slice.
        function mouseover(d){
            // call the update function of histogram with new data.
            hG.update(fData.map(function(v){ 
                return [v.fileType,v.freq[d.data.type]];}),segColor(d.data.type));
        }
        //Utility function to be called on mouseout a pie slice.
        function mouseout(d){
            // call the update function of histogram with all data.
            hG.update(fData.map(function(v){
                return [v.fileType,v.total];}), barColor);
        }
        // Animating the pie-slice requiring a custom function which specifies
        // how the intermediate paths should be drawn.
        function arcTween(a) {
            var i = d3.interpolate(this._current, a);
            this._current = i(0);
            return function(t) { return arc(i(t));    };
        }    
        return pC;
    }
    
    // function to handle legend.
    function legend(lD){
        var leg = {};
            
        // create table for legend.
        var legend = d3.select(id).append("table").attr('class','legend');
        
        // create one row per segment.
        var tr = legend.append("tbody").selectAll("tr").data(lD).enter().append("tr");
            
        // create the first column for each segment.
        tr.append("td").append("svg").attr("width", '16').attr("height", '16').append("rect")
            .attr("width", '16').attr("height", '16')
            .attr("fill",function(d){ return segColor(d.type); });
            
        // create the second column for each segment.
        tr.append("td").text(function(d){ return d.type;});
 
        // create the third column for each segment.
        tr.append("td").attr("class",'legendFreq')
            .text(function(d){ return d3.format(",")(d.freq);});
 
        // create the fourth column for each segment.
        tr.append("td").attr("class",'legendPerc')
            .text(function(d){ return getLegend(d,lD);});
 
        // Utility function to be used to update the legend.
        leg.update = function(nD){
            // update the data attached to the row elements.
            var l = legend.select("tbody").selectAll("tr").data(nD);
 
            // update the frequencies.
            l.select(".legendFreq").text(function(d){ return d3.format(",")(d.freq);});
 
            // update the percentage column.
            l.select(".legendPerc").text(function(d){ return getLegend(d,nD);});        
        }
        
        function getLegend(d,aD){ // Utility function to compute percentage.
            return d3.format("%")(d.freq/d3.sum(aD.map(function(v){ return v.freq; })));
        }
 
        return leg;
    }
    
    // calculate total frequency by segment for all state.
    var tF = ['Compressed','Uncompressed','Space_saved_after_Compression'].map(function(d){ 
        return {type:d, freq: d3.sum(fData.map(function(t){ return t.freq[d];}))}; 
    });    
    
    // calculate total frequency by state for all segment.
    var sF = fData.map(function(d){return [d.fileType,d.total];});
     
    var hG = histoGram(sF), // create the histogram.
        pC = pieChart(tF), // create the pie-chart.
        leg= legend(tF);  // create the legend.
}

function drawChart(list) {

  for ( var i = 0; i < list.length; i++) {
      var data = google.visualization.arrayToDataTable([
      ['Content', 'Size'],
      ['Res',  list[i].resFileSize[0]],
      ['Java Code',  list[i].dexFileSize[0]],
      ['Libraries', list[i].libraryFileSize[0]],
      ['Assets', list[i].assetsFileSize[0]],
      ['Resources',list[i].resourcesFileSize[0] ],
      ['Miscellaneous' , list[i].miscFileSize[0] ]
  ]);
  }

  var options = {
    title: apkName,
    is3D: true,
  };

  var chart = new google.visualization.PieChart(document.getElementById('displayChart'));
  chart.draw(data, options);
}

async function fileDisplayAndSearchQuery() {

  const response = await fetch("/retrieve_files");
  const apk_list  = await response.json();

  displayFiles(apk_list);
  createDataList(apk_list);

}

function createDataList(apks) {

  const searchList = document.getElementById('binary_files');
  apks.forEach((apk) => {searchList.appendChild(createSearchElement(apk.name));})
  
}

function displayFiles(apks) {

  const apkListElement = document.getElementById('display-files');
  apks.forEach((apk) => {apkListElement.appendChild(createApkElement(apk));})

}

function deleteAPK(fileName, fileOwnership) {

  const params = new URLSearchParams();
  params.append('fileName', fileName);
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


function createSearchElement(name) {

  const optionElement = document.createElement('option');
  optionElement.value = name;

  return optionElement;

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

    var val = document.getElementById("chart");
    var result = val.options[val.selectedIndex].value;
    showFileStatistics(apk.name, apk.time, result);
    
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

  displayed_apks[apk.name] = apkElement;

  return apkElement;
}

function reloadAboutPage() { location.replace("/#/about"); location.reload(); }

function redirect(){ location.replace("/#/explore"); location.reload(); }

function sizeUnitConversion(size){
    if (size >= 1000) {
        var byteToKb = size/1000; 

        if (byteToKb >= 1000) {
            var byteToMb = byteToKb/1000;
            return (byteToMb.toFixed(2)).toString()+" MB";
        }
        return (byteToKb.toFixed(2)).toString()+" KB";
    }
    return size.toString()+" Bytes";
}

//This function adds a background color to the Apk file type to make it easily distinguished
function createListAPKNameElement(texts) {
  const card = document.createElement("div");
  card.className = "card bg-primary text-dark";
  const cardBody = document.createElement("div");
  cardBody.className = "card-header";
  cardBody.innerText =("APK Name: " + texts);

  card.appendChild(cardBody);
  
  return card;
}

//This function adds a background color to the Apk Header to make it easily distinguished
function createListHeaderElement(texts) {
  const card = document.createElement("div");
  card.className = "card bg-secondary text-white";
  const cardBody = document.createElement("div");
  cardBody.className = "card-header";
  cardBody.innerText = texts;

  card.appendChild(cardBody);
  
  return card;
}

//This function adds a background color to the Apk compressed and uncompressed 
//size type to make it easily distinguished
function createListBodyElement(texts) {
  const card = document.createElement("div");
  card.className = "card bg-light text-dark";
  const cardBody = document.createElement("div");
  cardBody.className = "card-header";
  cardBody.innerText = texts;

  card.appendChild(cardBody);
  
  return card;
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
        createListAPKNameElement(apkName));
        contentListElement.appendChild(
        createListHeaderElement('Res '));
        contentListElement.appendChild(
        createListBodyElement(('Uncompressed: '+ sizeUnitConversion(list[i].resFileSize[0]))));
        contentListElement.appendChild(
        createListBodyElement(('Compressed: '+ sizeUnitConversion(list[i].resFileSize[1]))));
        contentListElement.appendChild(
        createListHeaderElement('Java Code '));
        contentListElement.appendChild(
        createListBodyElement(('Uncompressed: '+ sizeUnitConversion(list[i].dexFileSize[0]))));
        contentListElement.appendChild(
        createListBodyElement(('Compressed: '+ sizeUnitConversion(list[i].dexFileSize[1]))));
        contentListElement.appendChild(
        createListHeaderElement('Libraries '));
        contentListElement.appendChild(
        createListBodyElement(('Uncompressed: '+ sizeUnitConversion(list[i].libraryFileSize[0]))));
        contentListElement.appendChild(
        createListBodyElement(('Compressed: '+ sizeUnitConversion(list[i].libraryFileSize[1]))));
        contentListElement.appendChild(
        createListHeaderElement('Assets '));
        contentListElement.appendChild(
        createListBodyElement(('Uncompressed: '+ sizeUnitConversion(list[i].assetsFileSize[0]))));
        contentListElement.appendChild(
        createListBodyElement(('Compressed: '+ sizeUnitConversion(list[i].assetsFileSize[1]))));
        contentListElement.appendChild(
        createListHeaderElement('Resources '));
        contentListElement.appendChild(
        createListBodyElement(('Uncompressed: '+ sizeUnitConversion(list[i].resourcesFileSize[0]))));
        contentListElement.appendChild(
        createListBodyElement(('Compressed: '+ sizeUnitConversion(list[i].resourcesFileSize[1]))));
        contentListElement.appendChild(
        createListHeaderElement('Miscellaneous '));
        contentListElement.appendChild(
        createListBodyElement(('Uncompressed: '+   sizeUnitConversion(list[i].miscFileSize[0]))));
        contentListElement.appendChild(
        createListBodyElement(('Compressed: '+   sizeUnitConversion(list[i].miscFileSize[1]))));
        contentListElement.appendChild(
        createListHeaderElement(('Total: '+  sizeUnitConversion(list[i].totalApkSize))));
  }
}


/* scrollToAPK() retrieves the queried APK from a dictionary.
It then uses getBoundingClientRect() which keeps track of the
object's position at any given time to scroll to the APK's
position (scrolls the APK to the center of the page depending 
on its position) for user interaction. */
function scrollToAPK() {

  var apkListObject = displayed_apks[document.getElementById('search').value];
  var apkPosition = apkListObject.getBoundingClientRect();
  window.scrollTo(0, apkPosition.top - ( window.innerHeight / 2 ) );

  apkListObject.style.border = "thick solid blue";

  setTimeout(function() { apkListObject.style.border = "thin solid black"; }, 4000);
}

// Show the loader button once the form has been submitted
// until the page has finished loading
function displayLoader() {
  var loader = document.getElementById('btn-load');
  loader.style.display = "block";
}

// Calls the DexParserServlet for processing of DEX files and retrieves the statistics
async function exploreParser(filename, time) {
  const params = new URLSearchParams();
  params.append('fileName', filename);
  params.append('timeStamp', time);
  console.log(filename, time);

  const response = await fetch("/dexparser", {method: 'POST', body: params});
  const fileStats = await response.json();    
  console.log(fileStats);

  const dexButtonArea = document.getElementById("buttonDex");
  const dexButtonElement = document.createElement('button');
  dexButtonElement.className = 'btn btn-primary';
  dexButtonElement.innerText = 'Explore DEX File';
  dexButtonElement.setAttribute('id', 'button-dex');
  dexButtonElement.addEventListener('click', () => {
    displayParser(fileStats);
  });

  // Removes an already existing DEX explorer button
  if(document.body.contains(document.getElementById('button-dex'))) {
    dexButtonArea.removeChild(document.getElementById('button-dex'));
  } 

  dexButtonArea.appendChild(dexButtonElement);

  // Removes all existing children in the div
  const dexParserElement = document.getElementById("displayDexStats");
  while (dexParserElement.firstChild) {
    dexParserElement.removeChild(dexParserElement.firstChild);
  }

}

// Displays DEX statistics of the APK retrieved from Datastore
function displayParser(fileStats) {
  
  const dexParserElement = document.getElementById("displayDexStats");

  dexParserElement.innerHTML = '';

  for (var i = 0; i < fileStats.length; i++) {
    dexParserElement.appendChild(
      createListHeaderElement('DEX Statistics'));
    dexParserElement.appendChild(
      createListHeaderElement(('Header Size: ' + sizeUnitConversion(fileStats[i].headerSize))));
    dexParserElement.appendChild(
      createListHeaderElement(('File Size: ' + sizeUnitConversion(fileStats[i].fileSize))));
    dexParserElement.appendChild(
      createListHeaderElement(('StringIdsSize: ' + sizeUnitConversion(fileStats[i].stringIdsSize))));
    dexParserElement.appendChild(
      createListHeaderElement(('MethodIdsSize: ' + sizeUnitConversion(fileStats[i].methodIdsSize))));
    dexParserElement.appendChild(
      createListHeaderElement(('TypeIdsSize: ' + sizeUnitConversion(fileStats[i].typeIdsSize))));
    dexParserElement.appendChild(
      createListHeaderElement(('ProtoIdsSize: ' + sizeUnitConversion(fileStats[i].protoIdsSize))));
    dexParserElement.appendChild(
      createListHeaderElement(('FieldIdsSize: ' + sizeUnitConversion(fileStats[i].fieldIdsSize))));  
    dexParserElement.appendChild(
      createListHeaderElement(('ClassDefsSize: ' + sizeUnitConversion(fileStats[i].classDefsSize))));
  }

}

var displayed_apks = {};

exports.sizeUnitConversion = sizeUnitConversion;
exports.getFreqData = getFreqData;