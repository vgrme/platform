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

}).call(this);