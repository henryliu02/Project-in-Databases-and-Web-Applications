/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMoviesResult(resultData) {
    console.log("handleStarResult: populating movies table from resultData");
    console.log("handleStarResult function called with resultData: ", resultData);

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let genreList = jQuery("#genre-list");

    // Iterate through resultData
    for (let i = 0; i < resultData.length; i++) {
        var genre = resultData[i]["genreName"];
        var genreId = resultData[i]['genreId'];
        if (i % 4 == 0 && i > 0) {
            genreList.append('<br>');
        }
        genreList.append('<li style="display:inline-block;list-style-type:disc;margin-right:20px;">' + '<a href="genre.html?id=' + genreId + '">' + genre + '</a>' + '</li>');
    }

    let titleList = jQuery("#title-list");
    const alphabet = [...Array(26)].map((_, i) =>
        String.fromCharCode(65 + i));
    const num = ['0','1','2','3','4','5','6','7','8','9','*'];

    for (let i = 0; i < alphabet.length; i++) {
        var alpha = alphabet[i];
        if (i % 26 == 0 && i > 0) {
            titleList.append('<br>');
        }
        titleList.append('<li style="display:inline-block;list-style-type:disc;margin-right:20px;">' + '<a href="genre.html?title=' + alpha + '">' + alpha + '</a>' + '</li>');
    }

    titleList.append('<br>');
    for(let i = 0; i<num.length; i++){
        var numm = num[i];
        titleList.append('<li style="display:inline-block;list-style-type:disc;margin-right:20px;">' + '<a href="genre.html?title=' + numm  + '">' + numm + '</a>' + '</li>');
    }

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleMoviesResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/main", // Setting request url, which is mapped by MoviesServlet in MoviesServlet.java
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});