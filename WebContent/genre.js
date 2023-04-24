// $(document).ready(function() {
//     // get the current URL
//     var url = window.location.href;
//
//     // get the value of the "n" parameter from the URL, if it exists
//     var nValue = getParameterByName("n");
//
//     // set the selected option to the value of the "n" parameter, if it exists
//     // Get a reference to the select element
//     const selectElement = document.querySelector('select[name="n"]');
//
//     // Set the selected option value based on the parameter
//     selectElement.value = nValue;
//
//     // // update the URL with the selected option when the button is clicked
//     // $("#movie_table_button").click(function() {
//     //     var n = $("#movie_table_select").val();
//     //     var newUrl = updateUrlParameter(url, "n", n);
//     //     window.location.href = newUrl;
//     // });
// });


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

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    // starInfoElement.append("<p>" + resultData[0]["movie_title"] + "</p>");
    // starInfoElement.append("<span>" + "<a>" +'(' + resultData[0]["movie_year"] + ")" + "</span>" + "</a>")
    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" +
            '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">' + resultData[i]["movie_title"] +'</a>' + '</td>';
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_genres"] + "</td>";

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
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Try to get genreId and rowPerPage from session
let genreId, rowPerPage;

// Get genreId from URL parameter or sessionStorage
if (getParameterByName('id')) {
    genreId = getParameterByName('id');
    sessionStorage.setItem('genreId', genreId);
} else if (sessionStorage.getItem('genreId')) {
    genreId = sessionStorage.getItem('genreId');
}

// Get rowPerPage from URL parameter or sessionStorage
if (getParameterByName('n')) {
    rowPerPage = getParameterByName('n');
    sessionStorage.setItem('rowPerPage', rowPerPage);
} else if (sessionStorage.getItem('rowPerPage')) {
    rowPerPage = sessionStorage.getItem('rowPerPage');
} else {
    rowPerPage = 10; // Default to 10 if not present in parameter or sessionStorage
    sessionStorage.setItem('rowPerPage', rowPerPage);
}

console.log("genre id: ", genreId);
console.log("rows per page: ", rowPerPage);
console.log("current page: ", currentPage);


// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/genre?id=" + genreId +"&n=" + rowPerPage +"&page=" + currentPage,// Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

// jQuery.ajax({
//     dataType: "json",  // Setting return data type
//     method: "GET",// Setting request method
//     url: "api/genre?n=" + rowPerPage,// Setting request url, which is mapped by StarsServlet in Stars.java
//     success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
// });
//
// // Set the text of the active page link to the "page" value
// jQuery.ajax({
//     dataType: "json",  // Setting return data type
//     method: "GET",// Setting request method
//     url: "api/genre?page=" + currentPage,// Setting request url, which is mapped by StarsServlet in Stars.java
//     success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
// });







