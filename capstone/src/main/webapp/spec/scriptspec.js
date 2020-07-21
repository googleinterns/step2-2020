const script = require('../script');
const list=[ {
    resFileSize: [374475, 154337], 
    dexFileSize: [3489322, 12345],
    libraryFileSize: [4234, 3333],
    assetsFileSize: [773332, 12899],
    resourcesFileSize: [88732, 56467],
    miscFileSize: [747838, 12334]
}]

const freqData = [
    {fileType:'Res',freq:{compressed:154337, uncompressed: 374475, lost:(374475 - 154337)}},
    {fileType:'Java Code',freq:{compressed:12345, uncompressed:3489322, lost:(3489322 - 12345)}},
    {fileType:'Resources',freq:{compressed:56467, uncompressed:88732, lost:(88732 - 56467 )}},
    {fileType:'Miscellaneous',freq:{compressed:12334, uncompressed:747838, lost:(747838 - 12334)}},
    {fileType:'Assets',freq:{compressed:12899, uncompressed:773332, lost:(773332 - 12899)}}
]

describe("Unit Testing", function() {

    describe("conversion", function() {
        it("should return number in MB and add two decimal points",function() {   
             expect(script.sizeUnitConversion(4653735)).toEqual("4.65 MB"); 
        });
        it("should return number in KB and add two decimal points",function() {   
             expect(script.sizeUnitConversion(4735)).toEqual("4.74 KB"); 
        });
        it("should return number in Bytes and add two decimal points",function() {   
             expect(script.sizeUnitConversion(435)).toEqual("435 Bytes"); 
        });
    });

    describe("get Frequency data", function() {
        it("should return if freqency is defined",function() {   
            expect(script.getFreqData(list)).toEqual(freqData);
        });
    });
});
