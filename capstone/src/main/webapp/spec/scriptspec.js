const script = require('../script');
describe("scriptspec", function() {
    describe("conversion", function() {
        it("should return number in bytes or Kb and add two decimal points",function() {   
             expect(script.sizeUnitConversion(4653735)).toEqual("4.65 MB"); 
        });
    });
});