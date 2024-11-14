const form = document.querySelector(".messageForm");

form.addEventListener("submit", (event) => {
  // STOPS A FORM FROM SUBMITTING ALLOWING YOU TO HANDLE THE SUBMISSION WITH JAVASCRIPT INSTEAD
  event.preventDefault();
  let data = new FormData(event.target);


});