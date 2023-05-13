function handleMetadataResult(resultData) {
    console.log("handleMetadataResult: populating metadata from resultData");
    console.log("handleMetadataResult function called with resultData: ", resultData);

    let metadataContainer = jQuery("#metadata-container");

    for (const [tableName, columns] of Object.entries(resultData)) {
        let tableMetadata = '<h3>' + tableName + ':</h3><ul>';

        for (const column of columns) {
            tableMetadata += '<li>' + column + '</li>';
        }

        tableMetadata += '</ul>';
        metadataContainer.append(tableMetadata);
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "../api/index", // Setting request url, which is mapped by IndexServlet
    success: (resultData) => handleMetadataResult(resultData) // Setting callback function to handle data returned successfully by the IndexServlet
});
