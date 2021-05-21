(function() { // Avoid variables ending up in the global scope

    let questionNumber = createCounter();

    window.onload = function() { // Wait for the document to finish loading
        document.getElementById("addQuestionBtn").addEventListener("click", addQuestionField, false);
        document.getElementById("removeQuestionBtn").addEventListener("click", removeQuestionField, false);
    }

    function addQuestionField() {
        let questions = document.getElementById("questions");

        questionNumber.increment();

        // Create new question divs
        let columnsDiv = document.createElement("div");
        columnsDiv.setAttribute("class", "columns");
        columnsDiv.setAttribute("id", "questionDiv" + questionNumber.value());

        let columnDiv = document.createElement("div");
        columnDiv.setAttribute("class", "column field is-5");
        columnsDiv.appendChild(columnDiv);

        // Create new text label
        let questionLabel = document.createElement("label");
        questionLabel.setAttribute("for", "question" + questionNumber.value());
        questionLabel.setAttribute("class", "label");
        questionLabel.textContent = "Question " + questionNumber.value();
        columnDiv.appendChild(questionLabel);

        // Create new input field
        let controlDiv = document.createElement("div");
        controlDiv.setAttribute("class", "control");
        columnDiv.appendChild(controlDiv);

        let questionField = document.createElement("input");
        questionField.setAttribute("type", "text");
        questionField.setAttribute("class", "input");
        questionField.setAttribute("name", "question[]");
        questionField.setAttribute("id", "question" + questionNumber.value());
        questionField.setAttribute("maxlength", "45");
        questionField.required = true;
        controlDiv.appendChild(questionField);

        questions.appendChild(columnsDiv);
    }

    function removeQuestionField() {
        if (questionNumber.value() > 1) {
            let questions = document.getElementById("questions");
            let question = document.getElementById("questionDiv" + questionNumber.value());

            questionNumber.decrement();

            questions.removeChild(question);
        }
    }

    function createCounter() {
        let counter = 1;

        // ONLY FOR DEBUG PURPOSE.
        /*function display() {
            console.log("Number of " + counterName + ": " + counter);
        }*/

        function increment() {
            ++counter;
            //display();
        }

        function decrement() {
            --counter;
            //display();
        }

        return {
            increment : increment,
            decrement : decrement,
            value : () => counter // Same as function() { return counter; }
        };
    }
})();