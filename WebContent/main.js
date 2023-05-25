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


function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")

    // TODO: if you want to check past query results first, you can do it here

    // check past query results first
    let cachedData = localStorage.getItem(query);
    if (cachedData) {
        console.log("Using cached data");
        handleLookupAjaxSuccess(JSON.parse(cachedData), query, doneCallback);
        return;
    }

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/ft_search?title=" + escape(query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            console.log("sending AJAX request to backend Java Servlet")
            // Store to cache
            localStorage.setItem(query, JSON.stringify(data));
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // Check if data is already an object
    var jsonData = typeof data === "object" ? data : JSON.parse(data);
    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieID"])
    window.location.href = "single-movie.html?id=" + suggestion["data"]["movieID"];
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
    minChars: 3,
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
    // already handle in main.html using action keywords.
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})



