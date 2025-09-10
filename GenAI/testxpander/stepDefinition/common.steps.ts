/* eslint-disable no-undef */
/* eslint-disable no-await-in-loop */
/* eslint-disable @typescript-eslint/no-var-requires */

const { Given, When, Then } = require('@cucumber/cucumber');
const { assert, expect } = require('chai');
const Selectors1 = require('../support/loadDataAndLocators');
const actions = require('../support/common-actions');
const randGenerator = require('../support/getRandomData');
const { format, subDays, subMonths, subYears } = require('date-fns');
const date = require('../support/getDate');
const fs = require('fs');
const path = require('path');
const { config } = require('../../../conf/shared/wdio.shared');
const envConfig = require('../../../conf/shared/env.conf');

const savedValueList = new Map();
const currentDate = new Date();

Given(/^User is on application url "(.*)"$/, async (url) => {
  console.log("URL:", url)
  await browser.url(url); // Update to the specified URL
  await browser.pause(1000);
});

// Then(/^User navigate to following url "(.*)"$/, url => {
//   browser.url(url);
// });

Then(/^User is verifying the "(.*)" text contains "(.*)"$/,
async (element, expectedText) => {
    let expectedvalue = expectedText;
    expectedvalue = await Selectors1.getData(expectedText);
    let locatorString = await Selectors1.getSelector(element);
    const selector = $(locatorString);
    const actualText = await selector.getText();
    assert.include(actualText, expectedvalue);
  },
);

Then(/^User is verifying "(.*)" is displayed$/,async (element) => {
  const locatorString = Selectors1.getSelector(element);
  const selector = $(locatorString);
  const elementState = await selector.isDisplayed();
  assert.equal(elementState, true);
});

Then(/^User click on "(.*)" on the page$/, async (element) => {
  const locatorString = await Selectors1.getSelector(element);
  const selector = $(locatorString);
  await selector.click();
});

Then(/^User enters "(.*)" on "(.*)" on the page$/, async (element, testData) => {
  // const locatorString = Selectors1.getSelector(element);
  // const selector = $(locatorString);
  // let expectedvalue = Selectors1.getData(testData);
  actions.enter(testData,element);
});

Then(/^User executed accessibility rules on the page$/, async () => {
  // const locatorString = Selectors1.getSelector(element);
  // const selector = $(locatorString);
  // let expectedvalue = Selectors1.getData(testData);
  actions.runaccessibility();
});

Then(/^User runs page performance on the page$/, async () => {
  actions.runPagePerf();
});

Then(/^User is registered as light user$/, async () => {
  actions.runPagePerf();
});


Then('User run accessibility on the page', async () => {
  // Code to run accessibility checks on the page
});

Then('User run page performance', async () => {
  // Code to run page performance checks
});

Then('User register as light user on the application', async () => {
  // Code to register as a light user
});

Then('User click on save progress button', async () => {
  // Code to click on the save progress button
});

Then('User perform accessibility for Dashboard page', async () => {
  // Code to perform accessibility checks for the Dashboard page
});

Then('User verify logout link as per "(.*)"', async () => {
  // Code to verify the logout link
});

Then('User click on save progress button', async () => {
  // Code to click on the save progress button
});

Then('User verify Need some help text', async () => {
  // Code to verify the "Need some help" text
});

Then('User verify error message is displayed', async () => {
  // Code to verify that an error message is displayed
});

Then('User verify feedback section is present', async () => {
  // Code to verify that the feedback section is present
});

Then('User perform accessibility for feedback section', async () => {
  // Code to perform accessibility checks for the feedback section
});
