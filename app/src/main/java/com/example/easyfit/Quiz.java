package com.example.easyfit;

import java.util.List;

public class Quiz {
    private String id; // This will hold the quizId (Firebase key)
    private String title;
    private List<Question> questions;
    private int numberOfQuestions;

    public Quiz() {
        // Default constructor for Firebase
    }

    public Quiz(String title, int numberOfQuestions, List<Question> questions) {
        this.title = title;
        this.numberOfQuestions = questions != null ? questions.size() : 0;
        this.questions = questions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Quiz Title: ").append(title).append("\n")
                .append("Number of Questions: ").append(numberOfQuestions).append("\n")
                .append("Questions:\n");
        for (Question question : questions) {
            sb.append(question.toString()).append("\n");
        }
        return sb.toString();
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


    public List<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }


    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }
    public void setNumberOfQuestions(int numberOfQuestions) { this.numberOfQuestions = numberOfQuestions;}


    // Question Class
    public static class Question {
        private String questionText;
        private List<String> options;
        private String correctOption;
        private String explanation;
        private String isExplanationVisible; // Add this field to track visibility
        private boolean isAnswerCorrect;


        public Question() {
            // Default constructor for Firebase
        }

        public Question(String questionText, List<String> options, String correctOption, String explanation) {
            this.questionText = questionText;
            this.options = options;
            this.correctOption = correctOption;
            this.explanation = explanation;
        }

        @Override
        public String toString() {
            return "Question: " + questionText + "\nOptions: " + options + "\nCorrect Option: " + correctOption + "\nExplanation: " + explanation;
        }


        // Getters and Setters
        public String isExplanationVisible() { return isExplanationVisible; }
        public void setExplanationVisible(String explanationVisible) { isExplanationVisible = explanationVisible;}

        public boolean isAnswerCorrect() { return isAnswerCorrect; }
        public void isAnswerCorrect(boolean isAnswerCorrect) { isAnswerCorrect = isAnswerCorrect;}

        public String getQuestionText() {
            return questionText;
        }
        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }


        public List<String> getOptions() {
            return options;
        }
        public void setOptions(List<String> options) {
            this.options = options;
        }


        public String getCorrectOption() {
            return correctOption;
        }
        public void setCorrectOption(String correctOption) {
            this.correctOption = correctOption;
        }


        public String getExplanation() {
            return explanation;
        }
        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }
}

