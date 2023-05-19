/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
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
    starInfoElement.append("<p>" + resultData[0]["movie_title"] + "</p>");
    starInfoElement.append("<span>" + "<a>" +'(' + resultData[0]["movie_year"] + ")" + "</span>" + "</a>")
    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let row = document.createElement("tr");
        row.innerHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        row.innerHTML += "<td>" + resultData[i]["movie_director"] + "</td>";

        let genresHTML = "";
        let genres_id = resultData[i]["genres_id"].split(",");
        let genres = resultData[i]["movie_genres"].split(",");
        for(let j = 0; j < genres_id.length; j++){
            genresHTML += '<a href="genre.html?id=' + genres_id[j] + '">' + genres[j] + '</a>';
            if (j < genres_id.length - 1) {
                genresHTML += ", ";
            }
        }
        row.innerHTML += "<td>" + genresHTML + "</td>";

        let starsHTML = "";
        let stars = resultData[i]["movie_stars"].split(",");
        let stars_id = resultData[i]["stars_id"].split(",");

        for (let j = 0; j < stars_id.length; j++) {
            starsHTML += '<a href="single-star.html?id=' + stars_id[j] + '">' + stars[j] + '</a>';
            if (j < stars_id.length - 1) {
                starsHTML += ", ";
            }
        }

        row.innerHTML += "<td>" + starsHTML + "</td>";
        row.innerHTML += "<td>" + resultData[i]["movie_rating"] + "&nbsp;&star;" + "</td>";

        var button = document.createElement("button");
        button.className = "hover-effect-button"; // Add a class name to the button
        button.textContent = "Add Cart";
        // button.style.backgroundColor = "indigo";
        button.style.background = "linear-gradient(to bottom right, #CC2E5D, indigo)";
        button.style.color = "white";
        button.style.border = "none";
        button.style.padding = "10px 20px";
        button.style.borderRadius = "5px";
        button.style.fontFamily = "Helvetica Neue, Helvetica, Arial, sans-serif";
        button.style.fontSize = "10px";
        button.style.fontWeight = "bold";

        button.addEventListener("click", function() {
            console.log("button clicked");
            var movie = resultData[i]["movie_title"];
            var movie_id = resultData[i]["movie_id"];

            // Prepare data to send to the servlet
            var data = {
                id: movie_id,
                title: movie,
                action: "add_to_cart"
            };

            // Send a POST request to the ShoppingCartServlet
            $.ajax({
                type: "POST",
                url: "api/shoppingcart",
                data: data,
                success: function() {
                    // Show a popup message
                    alert("Movie added to cart!\nEnjoy!");
                    // Redirect to shopping_cart.html
                    // window.location.href = "shoppingcart.html";
                },
                error: function(error) {
                    console.error("Error:", error);
                }
            });
        });
        var buttonCell = document.createElement("td");
        buttonCell.appendChild(button);
        row.appendChild(buttonCell);

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(row);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');
console.log("starID: ", movieId)

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});


function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/ft_search?title=" + escape(query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            console.log("data: ", data)
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



