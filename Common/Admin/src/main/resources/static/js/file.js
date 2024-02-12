$(document).ready(function () {

    $("#fileImage").change(function () {
        if (!checkFileSize(this)) {
            return;
        }

        showImageThumbnail(this);

    });
});

function showImageThumbnail(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    };
    reader.readAsDataURL(file);
}

function checkFileSize(fileInput) {

    if (fileInput.files[0].size > 102400) {
        fileInput.setCustomValidity("Obrazek musi zajmować mniej niż 100KB!");
        fileInput.reportValidity();

        return false;
    } else {
        fileInput.setCustomValidity("");
        return true;
    }
}