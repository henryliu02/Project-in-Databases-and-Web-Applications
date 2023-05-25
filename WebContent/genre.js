// function to extract parameter name from url
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

    for (let i = 0; i < resultData.length; i++) {
        let row = document.createElement("tr");
        row.innerHTML = `
        <td><a href="single-movie.html?id=${resultData[i]["movie_id"]}">${resultData[i]["movie_title"]}</a></td>
        <td>${resultData[i]["movie_year"]}</td>
        <td>${resultData[i]["movie_director"]}</td>
    `;

        let genresHTML = "";
        let genres_id = resultData[i]["genres_id"].split(",");
        let genres = resultData[i]["movie_genres"].split(",");
        for(let j = 0; j < genres_id.length; j++){
            genresHTML += '<a href="genre.html?id=' + genres_id[j] + '">' + genres[j] + '</a>';
            if (j < genres_id.length - 1) {
                genresHTML += ", ";
            }
        }
        let genresCell = document.createElement("td");
        genresCell.innerHTML = genresHTML;
        row.appendChild(genresCell);

        let starsHTML = "";
        let stars = resultData[i]["movie_stars"].split(",");
        let stars_id = resultData[i]["stars_id"].split(",");
        for (let j = 0; j < stars_id.length; j++) {
            starsHTML += '<a href="single-star.html?id=' + stars_id[j] + '">' + stars[j] + '</a>';
            if (j < stars_id.length - 1) {
                starsHTML += ", ";
            }
        }
        let starsCell = document.createElement("td");
        starsCell.innerHTML = starsHTML;
        row.appendChild(starsCell);

        let ratingCell = document.createElement("td");
        ratingCell.innerHTML = resultData[i]["movie_rating"] + "&nbsp;&star;";
        row.appendChild(ratingCell);

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

        movieTableBodyElement.append(row);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Try to get genreId and rowPerPage, title from session
// check URL parameter for browsing
let genreId = "", rowPerPage = "", title = "", sort = "";

var call_1 = false;
// Get genreId from URL parameter or sessionStorage
if (getParameterByName('id') && !getParameterByName('title')) {
    genreId = getParameterByName('id');
    if (sessionStorage.getItem('genreId') != genreId) {
        sessionStorage.setItem('genreId', genreId);
        sessionStorage.removeItem("title");
        sessionStorage.removeItem("rowPerPage");
        sessionStorage.setItem("current_page", 1);
        sessionStorage.setItem("sort", 1);
    }
    call_1 = true;
}
else if (!getParameterByName('id') && getParameterByName('title')){
    title = getParameterByName('title');
    if (sessionStorage.getItem('title') != title) {
        sessionStorage.setItem('title', title);
        sessionStorage.removeItem("genreId");
        sessionStorage.removeItem("rowPerPage");
        sessionStorage.setItem("current_page", 1);
        sessionStorage.setItem("sort", 1);
    }
    call_1 = true;
}
else{
    genreId = sessionStorage.getItem('genreId');
    title = sessionStorage.getItem('title');
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

// get sort option from URL parameter or sessionStorage
if (getParameterByName('sort')) {
    sort = getParameterByName('sort');
    sessionStorage.setItem('sort', sort);
} else if (sessionStorage.getItem('sort')) {
    sort = sessionStorage.getItem('sort');
} else {
    sort = 1; // Default to sort option 1 if not present in parameter or sessionStorage
    sessionStorage.setItem('sort', sort);
}

// Add event listener to the "Previous" button
document.getElementById("prev-button").addEventListener("click", function(e) {
    e.preventDefault();
    currentPage = sessionStorage.getItem('current_page');

    // Update the current page number
    currentPage = currentPage > 1 ? currentPage - 1 : 1;
    sessionStorage.setItem('current_page', currentPage);


    // Clear the current page content
    document.getElementById("movie_table_body").innerHTML = "";
    // Reload the page with the updated parameter
    window.location.href = "genre.html?page=" + currentPage;
});

// Add event listener to the "Next" button
document.getElementById("next-button").addEventListener("click", function(e) {
    e.preventDefault();
    currentPage = sessionStorage.getItem('current_page');

    // Update the current page number
    currentPage++;
    sessionStorage.setItem('current_page', currentPage);

    // Clear the current page content
    document.getElementById("movie_table_body").innerHTML = "";
    // Reload the page with the updated parameter
    window.location.href = "genre.html?page=" + currentPage;
});


// Makes the HTTP GET request and registers on success callback function handleResult
// Function to call the first AJAX request
function callFirstAjax() {
    console.log("calling first ajax");
    console.log("genre id: ", genreId);
    console.log("title: ", title);
    console.log("rows per page: ", rowPerPage);
    currentPage = sessionStorage.getItem("current_page");
    console.log("current page: ", currentPage);
    console.log("current sort option: ", sort);
    sessionStorage.setItem("lastAjaxCall", 1);
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/genre?id=" + genreId + "&n=" + rowPerPage + "&page=" + currentPage + "&title=" + title + "&sort=" + sort,// Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// check URL parameter for searching
var call_2 = false;
let movieTitle = getParameterByName('title_');
let year = getParameterByName('year');
let director = getParameterByName('director');
let star = getParameterByName('star');
if(movieTitle != null || year != null || director != null || star != null){
    if( movieTitle != sessionStorage.getItem("title_") ||  year != sessionStorage.getItem("year")
        || director != sessionStorage.getItem("director") || star != sessionStorage.getItem("star") )
    {
        console.log("new search performed")
        sessionStorage.setItem("title_", movieTitle);
        sessionStorage.setItem("year", year);
        sessionStorage.setItem("director", director);
        sessionStorage.setItem("star", star);
        sessionStorage.removeItem("rowPerPage");
        sessionStorage.setItem("sort", 1);
        sessionStorage.setItem("current_page", 1);

        rowPerPage = 10;
        sort = 1;
        currentPage = 1;
    }
    call_2 = true;
}
movieTitle = sessionStorage.getItem("title_");
year = sessionStorage.getItem("year");
director = sessionStorage.getItem("director");
star = sessionStorage.getItem("star");


// Makes the HTTP GET request and registers on success callback function handleResult
function callSecondAjax() {
    console.log("calling second ajax");
    console.log("movie Title: ", movieTitle);
    console.log("Year: ", year);
    console.log("Director: ", director);
    console.log("Star: ", star);
    console.log("current page: ", currentPage);
    console.log("rows per page: ", rowPerPage);
    sessionStorage.setItem("lastAjaxCall", 2);
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/search?title=" + movieTitle + "&year=" + year + "&director=" + director + "&star=" + star + "&n=" + rowPerPage + "&page=" + currentPage + "&sort=" + sort,// Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

// Variable to store the last AJAX call made
let lastAjaxCall = sessionStorage.getItem("lastAjaxCall");
if (lastAjaxCall == null)
{
    if(genreId != null || title != null)
    {
        lastAjaxCall = 1;
        sessionStorage.setItem("lastAjaxCall", 1);
    }
    else if ( movieTitle != null || director != null || year != null || star != null){
        lastAjaxCall = 2;
        sessionStorage.setItem("lastAjaxCall", 2);
    }
}

if (call_1){
    console.log("new browse page");
    callFirstAjax();
}
else if (call_2){
    console.log("new search page");
    callSecondAjax();
}
else {
    if (lastAjaxCall == 1) {
        console.log("same browse page");
        callFirstAjax();
    } else {
        console.log("same search page");
        callSecondAjax();
    }
}



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

