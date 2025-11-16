package app.model.exercise;

import java.util.List;
import app.model.operation.BinaryOperation;
import app.service.QuestionGenerator;

public class SubtractionExercise extends Exercise  {

    public SubtractionExercise(){
        super();
    }

    /**
     * 生成习题
     */
    @Override
    public void generateExercise(int count) {
       QuestionGenerator generator = new QuestionGenerator();
       List<BinaryOperation> generatedProblems = generator.generateSubtractionQuestion(count);

       //直接将生成的题目列表添加到Exercise 的problems 字段中
       this.problems.clear();//如果之前有题目，先清空
       this.problems.addAll(generatedProblems);
    }


    
}
