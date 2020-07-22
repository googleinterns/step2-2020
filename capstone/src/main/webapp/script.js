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
        {fileType:'Res',freq:{compressed:res, uncompressed:uncomRes, lost:(uncomRes - res)}},
        {fileType:'Java Code',freq:{compressed:javaCode, uncompressed:uncomJavaCode, lost:(uncomJavaCode - javaCode)}},
        {fileType:'Resources',freq:{compressed:resources, uncompressed:uncomResources, lost:(uncomResources - resources )}},
        {fileType:'Miscellaneous',freq:{compressed:miscellaneous, uncompressed:uncomMiscellaneous, lost:(uncomMiscellaneous - miscellaneous)}},
        {fileType:'Assets',freq:{compressed:assets, uncompressed:uncomAssets, lost:(uncomAssets - assets)}}
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
        d3.select("#display").text(apkName);
        dashboard(getFreqData(fileStatistics));     
    }
    else if (result == 2){
        document.getElementById("display").style.display = 'none';
        document.getElementById("displayComponent").style.display= 'none';
        document.getElementById("displayChart").style.display = 'block'; 
        drawChart(fileStatistics);
    }
    else {
        document.getElementById("displayChart").style.display = 'none';
        document.getElementById("display").style.display = 'none';
        document.getElementById("displayComponent").style.display= 'block';
        document.createElement("h5").text(apkName);
        getDisplay(fileStatistics);
    }
}

async function showFileStatistics(filename, result) {
    const params = new URLSearchParams();
    params.append('apk_name', filename);
    apkName = filename;
    const response = await fetch("/display", {method: 'POST', body: params});
    const fileStatistics = await response.json();
    changeChart(fileStatistics,result);
    getDisplay(fileStatistics);
}

function dashboard(fData){

    var id = document.getElementById('display');
    d3.select("#display").text(apkName);
    var barColor = 'steelblue';
    function segColor(c){ return {compressed:"#807dba", uncompressed:"#e08214",lost:"#41ab5d"}[c]; }
    
    // compute total for each state.
    fData.forEach(function(d){d.total=(d.freq.uncompressed)});
    
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
    var tF = ['compressed','uncompressed','lost'].map(function(d){ 
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

function onSignIn(googleUser) {
  // Useful data for your client-side scripts:
  var profile = googleUser.getBasicProfile();
  console.log('Full Name: ' + profile.getName());
}

function displayFiles() {
  fetch('/retrieve_files').then(response => response.json()).then((apks) => {
    const apkListElement = document.getElementById('display-files');
    apks.forEach((apk) => {
      apkListElement.appendChild(createApkElement(apk));
    })
  });
}

function deleteAPK(fileName) {
  const params = new URLSearchParams();
  params.append('file_name', fileName);
  fetch('/delete_file', {method: 'POST', body: params});
}

// logInStatus is called on every page to ensure
// certain privileges are enjoyed by users who
// choose to be verified.

async function logInStatus() {
  const response = await fetch('/logon', {method: 'POST'});
  const isLoggedIn = await response.text();
  if (isLoggedIn.trim() == "true") {
    document.getElementById('login').style.display = "none";
    return;
  }
  document.getElementById('logout').style.display = "none";
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
    var val = document.getElementById("chart");
    var result = val.options[val.selectedIndex].value;
    showFileStatistics(apk.name, result);
  });

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.className = 'btn btn-primary';
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteAPK(apk.name);

    // Remove the apk from the DOM.
    apkElement.remove();
  });

  apkElement.appendChild(nameElement);
  apkElement.appendChild(exploreButtonElement);
  apkElement.appendChild(deleteButtonElement);

  return apkElement;
}

function redirect(){
    displayFiles();
}

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

function getDisplay(list) {
  // list is an arraylist containing strings, so we have to
  // reference its elements to create HTML content
  const contentListElement = document.getElementById("displayComponent");
  contentListElement.innerHTML = '';
    
  for (var i = 0; i < list.length; i++) {
        contentListElement.appendChild(
        createListElement(('Uncompressed Res: '+ sizeUnitConversion(list[i].resFileSize[0]))));
        contentListElement.appendChild(
        createListElement(('Compressed Res: '+ sizeUnitConversion(list[i].resFileSize[1]))));
        contentListElement.appendChild(
        createListElement(('Uncompressed Java Code: '+ sizeUnitConversion(list[i].dexFileSize[0]))));
        contentListElement.appendChild(
        createListElement(('Compressed Java Code: '+ sizeUnitConversion(list[i].dexFileSize[1]))));
        contentListElement.appendChild(
        createListElement(('Uncompressed Libraries: '+ sizeUnitConversion(list[i].libraryFileSize[0]))));
        contentListElement.appendChild(
        createListElement(('Compressed Libraries: '+ sizeUnitConversion(list[i].libraryFileSize[1]))));
        contentListElement.appendChild(
        createListElement(('Uncompressed Assets: '+ sizeUnitConversion(list[i].assetsFileSize[0]))));
        contentListElement.appendChild(
        createListElement(('Compressed Assets: '+ sizeUnitConversion(list[i].assetsFileSize[1]))));
        contentListElement.appendChild(
        createListElement(('Uncompressed Resources: '+ sizeUnitConversion(list[i].resourcesFileSize[0]))));
        contentListElement.appendChild(
        createListElement(('Compressed Resources: '+ sizeUnitConversion(list[i].resourcesFileSize[1]))));
        contentListElement.appendChild(
        createListElement(('Uncompressed Miscellaneous: '+   sizeUnitConversion(list[i].miscFileSize[0]))));
        contentListElement.appendChild(
        createListElement(('Compressed Miscellaneous: '+   sizeUnitConversion(list[i].miscFileSize[1]))));
        contentListElement.appendChild(
        createListElement(('Total: '+  sizeUnitConversion(list[i].totalApkSize))));
  }
}

exports.sizeUnitConversion = sizeUnitConversion;
exports.getFreqData = getFreqData;

