
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleCartResult( resultData ){
    document.getElementById("error").style.visibility = "hidden";
    let movIt = jQuery("#cart_table_body");
    var movie;
    if( resultData.length != 0){
        var sum = 0;
        for (i = 0; i < resultData.length; ++i) {
            movie = "<tr><th>" + '<a href="single_movie.html?id=' + resultData[i].ID + '">'
                + resultData[i].TITLE + '   </a>'+"<button onclick=removeCart({'type':'2','item':'"+resultData[i].ID+"'});window.history.go(0)>➖</button><br></th>" // link to singleMovie.html
                + "<th>" + resultData[i].YEAR + "</th>"
                + "<th>" + resultData[i].DIRECTOR + "</th>"
                // text field for item quantity
                + "<th>" + "<input type = 'number' onchange=changeAmount('"+resultData[i].ID+"') value='"+resultData[i].AMOUNT+"' min = '0', max='10' id='"+resultData[i].ID+"'>"
                //"<input class = 'amount' name="+resultData[i].ID+" type='number' onchange='changeAmount('"+resultData[i].ID+"')' " +
                //	"value='"+resultData[i].AMOUNT+"' min='0' max='10'>" +
                +	"</th><th>" + resultData[i].AMOUNT * 300 + "</th></tr>";
            sum += resultData[i].AMOUNT * 300;
            movIt.append(movie); // append
        }
        jQuery("#container").append("<div>Total Price :"+sum+"</div>");
        $("#proceed_link").text("Checkout")
    } else {
        jQuery("#cart_info").append("<div align='center'>Hon, Your cart is empty! Go and grab something</div>");
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');
console.log("starID: ", starId)

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/shopping-cart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});