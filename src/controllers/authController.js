const jwt = require('jsonwebtoken');
const User = require('../models/User');
const OTP = require('../models/OTP');

const generateToken = (id) => {
  return jwt.sign({ id }, process.env.JWT_SECRET, {
    expiresIn: process.env.JWT_EXPIRES_IN
  });
};

const generateOTP = () => {
  return Math.floor(100000 + Math.random() * 900000).toString();
};

// Send OTP
const sendOTP = async (req, res) => {
  try {
    const { phone } = req.body;

    if (!phone) {
      return res.status(400).json({ success: false, message: 'Phone required' });
    }

    const otp = generateOTP();
    const expiresAt = new Date(Date.now() + 10 * 60 * 1000); // 10 min

    await OTP.findOneAndUpdate(
      { phone },
      { otp, expiresAt, verified: false },
      { upsert: true, new: true }
    );

    // TODO: Send OTP via Twilio/SMS
    console.log(`OTP for ${phone}: ${otp}`);

    res.status(200).json({ success: true, message: 'OTP sent' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// Verify OTP + Login/Register
const verifyOTP = async (req, res) => {
  try {
    const { phone, otp } = req.body;

    const otpDoc = await OTP.findOne({ phone, otp, verified: false });

    if (!otpDoc) {
      return res.status(400).json({ success: false, message: 'Invalid OTP' });
    }

    if (otpDoc.expiresAt < new Date()) {
      return res.status(400).json({ success: false, message: 'OTP expired' });
    }

    otpDoc.verified = true;
    await otpDoc.save();

    let user = await User.findOne({ phone });
    if (!user) {
      user = await User.create({ phone });
    }

    res.status(200).json({
      success: true,
      token: generateToken(user._id),
      user: {
        id: user._id,
        phone: user.phone,
        plan: user.plan,
        dailyLimit: user.dailyLimit
      }
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

module.exports = { sendOTP, verifyOTP };
