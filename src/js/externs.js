var Highcharts;
Highcharts.Chart = function(){};

//for Handsontable
var Handsontable;
Handsontable.imageRenderer = function (instance, td, row, col, prop, value, cellProperties) {
    var escaped = Handsontable.helper.stringify(value), img;

    if (escaped.indexOf('http') === 0) {
      img = document.createElement('IMG');
      img.src = value;

      Handsontable.Dom.addEvent(img, 'mousedown', function (e){
        e.preventDefault(); // prevent selection quirk
      });

      Handsontable.Dom.empty(td);
      td.appendChild(img);
    }
    else {
      // render as text
      Handsontable.renderers.TextRenderer.apply(this, arguments);
    }

    return td;
}