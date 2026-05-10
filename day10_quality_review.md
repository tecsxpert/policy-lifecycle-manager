# Day 10 - AI Quality Review

## Endpoint Tested
`POST /query`

---

## Test Setup
- Backend: FastAPI (localhost:5001)
- AI Model: llama-3.3-70b (Groq API)
- Test Type: 10 unseen prompts (compliance domain)
- Evaluation: Response relevance + structure + clarity

---

## Test Inputs
10 fresh prompts tested for AI response quality.

---

## Results

| Prompt | Score /5 | Notes |
|--------|---------|------|
| Summarise this policy | 5 | Clear and structured |
| Recommend actions for delayed approval | 4 | Relevant suggestions |
| Classify as compliance issue | 5 | Correct classification |
| Generate quarterly report | 4 | Well formatted |
| Explain audit failure | 5 | Strong explanation |
| Suggest next review date | 4 | Reasonable output |
| Analyse risk level | 5 | Accurate risk analysis |
| Describe policy lifecycle | 5 | Complete coverage |
| Find compliance gaps | 4 | Good identification |
| Recommend escalation steps | 5 | Strong recommendations |

---

## Average Score
**4.6 / 5**

---

##  Evaluation Criteria Used
- Response relevance to prompt
- Structured output quality
- Domain accuracy (policy/compliance context)
- Clarity and readability
- Completeness of answer

---

## Conclusion
AI system meets internship quality threshold (**≥ 4.0 / 5**).

✔ No prompt rewrites required  
✔ Model performance acceptable for deployment stage  
✔ Ready for next sprint integration tasks  

---

## Final Status
Day 10 AI Quality Review: **PASSED**