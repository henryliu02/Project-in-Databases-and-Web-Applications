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
    let starTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" +
            '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">' + resultData[i]["movie_title"] +'</a>' + '</td>';
        rowHTML += "<td>" +
            '<a>' + resultData[i]["movie_year"] +'</a>' + '</td>';
        rowHTML += "<td>" +
            '<a>' + resultData[i]["movie_director"] +'</a>' + '</td>';
        rowHTML += "<td>" +
            '<a>' + resultData[i]["movie_genres"] +'</a>' + '</td>';

        let starsHTML = "";
        let stars = resultData[i]["movie_stars"].split(",");
        let stars_id = resultData[i]["stars_id"].split(",");

        for (let j = 0; j < stars_id.length; j++) {
            // console.log("star id for parsing into url: ", stars_id[j])
            starsHTML += '<a href="single-star.html?id=' + stars_id[j] + '">' + stars[j] + '</a>';
            if (j < stars_id.length - 1) {
                starsHTML += ", ";
            }
        }
        rowHTML += "<td>" + starsHTML + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_rating"] + "&nbsp;&star;" + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleMoviesResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by MoviesServlet in MoviesServlet.java
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});