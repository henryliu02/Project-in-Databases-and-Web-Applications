
function handleResult(resultData) {




}
handleSessionData(resultDataString)
{
    let resultDataJson = JSON.parse(resultDataString);
    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);
    handleMovieArray(resultDataJson["previousMovies"]);
}
function handleMovieArray(resultArray) {
    console.log(resultArray);
    let movie_list = $("#movie_list");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    movie_list.html("");
    movie_list.append(res);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');
console.log("movieID: ", movieId)
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "POST",// Setting request method
    url: "api/shopping-cart?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/shopping-cart?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: handleSessionData // Setting callback function to handle data returned successfully by the SingleStarServlet
});