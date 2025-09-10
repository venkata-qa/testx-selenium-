@download
Feature: User Should be able to download any file from application to our framework

  Scenario:S1-User Should be able to download any file from application to our framework
    Given I am in App main site
    Then  I click the "file_download_link" on the "HomePage"
    Then  I click the "test_file" on the "HomePage"
    Then  I wait "5" seconds to synchronize the things on app

    #    For testing purpose, we get and read the headers of already downloaded excel
#    but we can download the excel file at run time as per application functionality.
#    By uncommenting below 3 steps
  @DownloadTEST
  Scenario:S2-Verify header of excel file
    Given I am in App main site
    Then  I click the "file_download_link" on the "HomePage"
    Then  I click the "test_file" on the "HomePage"
    Then  I wait "5" seconds to synchronize the things on app
    Then Get headers from downloaded file





