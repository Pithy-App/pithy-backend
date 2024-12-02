package com.pithy.openai

object PlayGround {
  def main(args: Array[String]): Unit = {
    println("hey")
    //    implicit val system: ActorSystem = ActorSystem()
    //    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    //    val service: OpenAIService = OpenAIServiceFactory()
    //    val createChatCompletionSettings = CreateChatCompletionSettings(
    //      model = ModelId.gpt_3_5_turbo
    //    )
    //
    //        val messages = Seq(
    //          SystemMessage("You are a helpful assistant."),
    //          UserMessage("Who won the world series in 2020?"),
    //          AssistantMessage("The Los Angeles Dodgers won the World Series in 2020."),
    //          UserMessage("Where was it played?"),
    //        )
    //
    //        service.createChatCompletion(
    //          messages = messages,
    //          settings = createChatCompletionSettings
    //        ).map { chatCompletion =>
    //          println(chatCompletion.choices.head.message.content)
    //        }
  }
}
