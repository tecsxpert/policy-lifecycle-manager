PROMPTS = {

    "categorise": """
Classify the following policy text into one category only.

Allowed categories:
- Compliance
- Security
- HR
- Finance
- Operations

Rules:
- Return valid JSON only
- Do not explain anything
- Do not add markdown
- Do not add extra text

Return format:
{{
  "category": "string",
  "confidence": 0.0,
  "reasoning": "short reason"
}}

Text:
{text}
""",

    "recommend": """
You are a policy governance assistant.

Give exactly 3 professional recommendations for the policy text.

Rules:
- Do NOT explain JSON
- Do NOT describe formatting
- Do NOT mention API response structure
- Do NOT mention valid JSON instructions
- Return recommendations only
- Keep recommendations concise and professional
- Each recommendation must be practical

Return valid JSON only:
[
  {{
    "action_type": "string",
    "description": "string",
    "priority": "High/Medium/Low"
  }}
]

Text:
{text}
""",

    "generate_report": """
Generate a professional policy lifecycle report.

Rules:
- Keep response professional
- Do not add markdown code blocks
- Do not explain formatting
- Do not mention AI limitations
- Make output demo-ready

Include:
- title
- executive_summary
- overview
- top_items
- recommendations

Text:
{text}
"""
}