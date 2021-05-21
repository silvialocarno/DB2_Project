(function() {
    window.addEventListener("load", () => {

        let questionnaire = document.getElementById("questionnaire")
        let marketing_questions = document.getElementById("marketing_questions")
        let statistical_questions = document.getElementById("statistical_questions")
        let prev_button = document.getElementById("prev_button")
        let next_button = document.getElementById("next_button")
        let submit_button = document.getElementById("submit_button")
        
        // hide what to hide at startup
        statistical_questions.style.display = "none"
        prev_button.style.display = "none"
        submit_button.style.display = "none"

        next_button.addEventListener('click', (e) => {
            if(!questionnaire.checkValidity()) { // check if all required field have been compiled
                questionnaire.reportValidity(); // show warning if some fields are not compiled
            } else {
                marketing_questions.style.display = "none"
                next_button.style.display = "none"
                statistical_questions.style.display = "block"
                prev_button.style.display = "inline"
                submit_button.style.display = "inline"
            }
        })

        prev_button.addEventListener('click', (e) => {
            marketing_questions.style.display = "block"
            next_button.style.display = "inline"
            statistical_questions.style.display = "none"
            prev_button.style.display = "none"
            submit_button.style.display = "none"
        })
        
    }, false)
})();