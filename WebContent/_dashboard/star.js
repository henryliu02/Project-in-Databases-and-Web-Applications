let submit_form = $("#submit_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleResult(resultData) {
    if (resultData === "-1") {
        $("#login_error_message").text("Failed to add the star.");
    } else {
        $("#login_error_message").text("Star added successfully with StarId: " + resultData);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitSubmit_form(formSubmitEvent) {
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "../api/addStar", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: submit_form.serialize(),
            success: function(data, textStatus, jqXHR) {
                handleResult(jqXHR.responseText);
            }
        }
    );
}


// Bind the submit action of the form to a handler function
submit_form.submit(submitSubmit_form);

