Matthew Muriel

**Assignment 6**
-------------------

https://github.com/mistralmint/assignment6

In the portion for manual UI testing, I spent time writing
a Playwright test script in Java with JUnit. In this process,
I had to define every interaction step with my own actions.
I had to navigate to the website in the test browser,
 locate elements, and assert several times that the correct
texts were appearing. This took a lot of trial and error and therefore
plenty of time, but, the end result worked and was stable.
I was able to control and better understand what each step
of the test was doing. Some maintenance may be required if
the page layout changed, but the test is otherwise reliable.

Meanwhile, the AI-assisted UI testing, done through the Playwright MCP,
focused on properly prompting an AI on my end.
Rather than write any more code, I described the workflow
of what I was looking for with the test website. The AI
then generated a full Playwright test in Java, which of course
had significantly less effort required to writing tests. The generated
code was more complex than what I would have initially written myself too.
However, the initially generated code did not always match the website.
When I ran the BookStoreMCP test that it generated code for,
it was able to compile and launch all fine, but it failed midway.
This showed how AI can quickly set up tests that seem to work, but
they may not be consistently reliable, especially for
navigating a dynamic website through multiple pages of steps.

Overall, the manual UI testing felt easier to debug and was just more
accurate in the end. The AI-assisted testing was much faster to set up, but
needed adjustments anyway. Perhaps using AI to generate the tests
at first and lay out a base/draft is a more reliable way to go about it.