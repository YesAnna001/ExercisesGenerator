// package app.model.exercise;

// import java.util.List;

// import app.model.operation.BinaryOperation;
// import app.service.QuestionGenerator;

// /**
//  *      加法专项练习
//  */
// public class AdditionExercise extends Exercise {
//     private int maxSum; // 和的最大值
    
//     public AdditionExercise(int maxSum) {
//         super();
//         this.maxSum = maxSum;
//     }
    
//     // 重写方法：生成指定习题数量的习题集
//     @Override
//     public void generateExercise(int count) {
//         QuestionGenerator generator = new QuestionGenerator();
//         // 生成习题集，传入指定的习题数量和结果的最大值
//         List<BinaryOperation> problems = generator.generateAdditionQuestions(count, maxSum);
//         for (BinaryOperation problem : problems) {
//             addProblem(problem);
//         }
//     }
// }

