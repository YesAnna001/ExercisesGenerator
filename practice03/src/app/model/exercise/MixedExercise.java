package app.model.exercise;

import java.util.List;

import app.model.operation.BinaryOperation;
import app.service.QuestionGenerator;

// 混合运算练习（100以内）
public class MixedExercise extends Exercise {
    
    public MixedExercise() {
        super();
    }
    
    @Override
    public void generateExercise(int count) {
        QuestionGenerator generator = new QuestionGenerator();
        List<BinaryOperation> problems = generator.generateMixedQuestions(count);
        for (BinaryOperation problem : problems) {
            addProblem(problem);
        }
    }
}
