from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/describe', methods=['POST'])
def describe_policy():
    data = request.get_json()
    policy_text = data.get('policy_text', '').lower()

    # Simple keyword-based parsing
    if 'car insurance' in policy_text:
        policy_type = 'Car Insurance'
        benefits = ['Third Party Liability', 'Zero Depreciation', 'Roadside Assistance']
    elif 'life insurance' in policy_text:
        policy_type = 'Life Insurance' 
        benefits = ['Death Benefit', 'Critical Illness Rider', 'Tax Benefits']
    elif 'fire insurance ' in policy_text:
        policy_type = 'fire insurance'
        benefits = ['Fire Coverage', 'Theft Protection', 'Natural Disaster Cover']
    elif 'travel insurance' in policy_text:
        policy_type = 'Travel Insurance'
        benefits = ['Medical Cover', 'Trip Cancellation', 'Lost Baggage Cover']
    elif 'corporate insurance' in policy_text or 'employee health' in policy_text:
        policy_type = 'Corporate Health Insurance'
        benefits = ['Employee Medical Cover', 'Cashless Treatment', 'Annual Health Checkup']
    else:
        policy_type = 'Health Insurance'
        benefits = ['Hospitalization Cover', 'Cashless Treatment', 'Day Care Procedures']

    return jsonify({
        "Policy Type": policy_type,
        "Coverage Summary": data.get('policy_text', ''),
        "Key Benefits": benefits,
        "Exclusions": "Not specified",
        "Validity": "Not specified"
    })

if __name__ == '__main__':
    app.run(debug=True)

