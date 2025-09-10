Feature: Compare two shirt products test

Scenario: Compare two shirt products
Given User is on the "Home_Page" page
When I enter text "<shirt>" in "<Search_Field_Element>" field on "Home_Page" page
And I click on "<Search_Icon_Element>" on "Home_Page" page
And I assert page title text with "<Search Item>" on "Result_Page" page
And I click on "<Add_To_Compare_Icon_Item1_Element>" on "Result_Page" page
And I click on "<Add_To_Compare_Icon_Item2_Element>" on "Result_Page" page
And I click on "<Add_To_Compare_Icon_Item3_Element>" on "Result_Page" page
And I click on "<Add_To_Compare_Icon_Item4_Element>" on "Result_Page" page
And I click on "<Add_To_Compare_Icon_Item5_Element>" on "Result_Page" page
And I click on "<Add_To_Compare_Icon_Item6_Element>" on "Result_Page" page
And I assert element "<Item1_Label_Element>" text with "<Circe Hooded Ice Fleece>" on "Result_Page" page
And I assert element "<Item2_Label_Element>" text with "<Balboa Persistence Tee>" on "Result_Page" page
And I assert element "<Item3_Label_Element>" text with "<Circe Hooded Ice Fleece>" on "Result_Page" page
And I assert element "<Item4_Label_Element>" text with "<Balboa Persistence Tee>" on "Result_Page" page
And I assert element "<Item5_Label_Element>" text with "<Circe Hooded Ice Fleece>" on "Result_Page" page
And I assert element "<Item6_Label_Element>" text with "<Balboa Persistence Tee>" on "Result_Page" page
And I click on "<Compare_Button_Element>" on "Compare_Page" page
Then I assert element "<Compared_View_Element>" text with "<This is the Compared View.>" on "Compare_Page" page