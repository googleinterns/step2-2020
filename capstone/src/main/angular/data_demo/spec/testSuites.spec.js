const script = require('./script.js');
describe("testSuites", function() {
    describe("conversion", function() {
        it("should return number in bytes or Kb and add two decimal points",function() {   
             expect(script.sizeUnitConversion("4,653,735")).toEqual("4.65 MB"); 
        });
    });
});