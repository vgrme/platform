/*!
 * Geduca v1.0 (http://www.geduca.org)
 * Copyright 2014 Geduca, Org.
 */

(function () {

    $(function () {

        $('.list-group-item').on('click',function(e){
            var previous = $(this).closest(".list-group").children(".active");
            previous.removeClass('active'); // previous list-item
            $(e.target).addClass('active'); // activated list-item

            //$('.thumbnail').addClass('active')
        });

    });

    $(document).on("click", ".add-right-wrong-box", function (event) {
    	$this = $(this);

    	var totalQuestions;
    	totalQuestions = $('#quiz').children().length;
    	totalQuestions++;

    	var newQuestion;
      	newQuestion = "<div class='right-wrong box-question'>" +
	      				"<div class='question col-lg-12'>" +
	                      "<div class='col-lg-1 number'>" +
	                        "<label id='number-"+totalQuestions+"'>" + totalQuestions + "</label>" +
	                      "</div>" +
	                      "<textarea id='txt-"+totalQuestions+"'class='col-lg-10 question text' placeholder='Digite aqui sua pergunta...'>" +
	                      "</textarea>" +
	                      "<a href='#' class='col-lg-1 remove remove-right-wrong-box btn fa fa-times' role='button'></a>"+
	                    "</div>" +
	                    "<div class='answer col-lg-12'>" +
	                      "<div class='col-lg-offset-1 col-lg-10 option'>" +
	                        "<label class='col-lg-2 btn option'>" +
	                          "<input type='radio' name='mode'>" + " C " +
	                        "</label>" +
	                       "<textarea class='col-lg-10 answer text' placeholder='Digite aqui sua resposta...'>" +
	                       "</textarea>" +
	                      "</div>" +
	                      "<div class='col-lg-offset-1 col-lg-10 option'>" +
	                        "<label class='col-lg-2 btn option'>" +
	                          "<input type='radio' name='mode'>" + " E " +
	                        "</label>" +
	                        "<textarea class='col-lg-10 answer text' placeholder='Digite aqui sua outra resposta...'>" +
	                       "</textarea>" +
	                      "</div>" +
	                    "</div>" +
	                  "</div>" +
	                "</div>";

        $('.quiz').append(newQuestion);

        $(".txt_" + totalQuestions).focus();

    });

     $(document).on("click", ".add-judge-image-box", function (event) {
        $this = $(this);

        var totalQuestions;
        totalQuestions = $('#quiz').children().length;
        totalQuestions++;

        var newQuestion;
        newQuestion = "<div class='judge-image box-question'>" +
                        "<div class='question'>" +
                          "<div class='col-md-1 number'>" +
                            "<label id='number-"+totalQuestions+"'>" + totalQuestions + "</label>" +
                          "</div>" +
                          "<textarea id='txt-"+totalQuestions+"'class='col-md-10 question text' placeholder='Digite aqui sua pergunta...'>" +
                          "</textarea>" +
                          "<a href='#' class='col-md-1 remove remove-right-wrong-box btn fa fa-times-circle' role='button'></a>"+
                        "</div>" +
                        "<div class='answer'>" +
                          "<div class='col-md-11 option'>" +
                            "<label class='col-md-2 btn option'>" +
                              "<input type='radio' name='mode'>" + " C " +
                            "</label>" +
                           "<textarea class='col-md-9 answer text' placeholder='Digite aqui sua resposta...'>" +
                           "</textarea>" +
                          "</div>" +
                          "<div class='col-md-11 option'>" +
                            "<label class='col-md-2 btn option'>" +
                              "<input type='radio' name='mode'>" + " E " +
                            "</label>" +
                            "<textarea class='col-md-9 answer text' placeholder='Digite aqui sua outra resposta...'>" +
                           "</textarea>" +
                          "</div>" +
                        "</div>" +
                      "</div>" +
                    "</div>";

        $('.quiz').append(newQuestion);

        $(".txt_" + totalQuestions).focus();

    });

	$(document).on("click", ".remove-right-wrong-box", function (event) {
    	
    	$this = $(this);
    	$this.closest('.box-question').remove();

    	var questionsNumbers;
    	 questionsNumbers = $('#quiz .number').children();

    	for (var i = 0; i < questionsNumbers.length; i++) { 
    		$(questionsNumbers[i]).id = 'number-' + (i+1);
    		$(questionsNumbers[i]).context.innerText = (i+1);
    	}

    });


}).call(this);

