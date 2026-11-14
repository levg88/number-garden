#!/usr/bin/env swift
// Renders the bundled Number Garden lesson videos.
// macOS only (AppKit). The resulting MP4s are checked in so the app build
// itself needs no media tooling.
//
// Usage: swift tools/render_media.swift tools/out
import AppKit
import Foundation

let canvasW = 1280
let canvasH = 720
let fps = 24

let cream = NSColor(calibratedRed: 0.980, green: 0.961, blue: 0.914, alpha: 1)
let ink = NSColor(calibratedRed: 0.133, green: 0.188, blue: 0.165, alpha: 1)
let green = NSColor(calibratedRed: 0.184, green: 0.365, blue: 0.275, alpha: 1)
let muted = NSColor(calibratedRed: 0.290, green: 0.353, blue: 0.314, alpha: 1)
let outline = NSColor(calibratedRed: 0.847, green: 0.808, blue: 0.733, alpha: 1)
let panel = NSColor.white
let appleRed = NSColor(calibratedRed: 0.788, green: 0.337, blue: 0.294, alpha: 1)
let appleDark = NSColor(calibratedRed: 0.620, green: 0.231, blue: 0.200, alpha: 1)
let leaf = NSColor(calibratedRed: 0.361, green: 0.486, blue: 0.310, alpha: 1)
let stem = NSColor(calibratedRed: 0.420, green: 0.290, blue: 0.208, alpha: 1)
let amber = NSColor(calibratedRed: 0.722, green: 0.455, blue: 0.122, alpha: 1)
let flowerRose = NSColor(calibratedRed: 0.788, green: 0.478, blue: 0.451, alpha: 1)
let flowerCenter = NSColor(calibratedRed: 0.894, green: 0.718, blue: 0.357, alpha: 1)

func font(_ size: CGFloat) -> NSFont {
    NSFont(name: "Arial Rounded MT Bold", size: size) ?? NSFont.boldSystemFont(ofSize: size)
}

func topRect(_ x: CGFloat, _ top: CGFloat, _ w: CGFloat, _ h: CGFloat) -> NSRect {
    NSRect(x: x, y: CGFloat(canvasH) - top - h, width: w, height: h)
}

func fillRounded(_ rect: NSRect, radius: CGFloat, color: NSColor, stroke: NSColor? = nil, lineWidth: CGFloat = 0) {
    let path = NSBezierPath(roundedRect: rect, xRadius: radius, yRadius: radius)
    color.setFill()
    path.fill()
    if let stroke {
        stroke.setStroke()
        path.lineWidth = lineWidth
        path.stroke()
    }
}

func drawText(_ text: String, size: CGFloat, color: NSColor, top: CGFloat,
              x: CGFloat, width: CGFloat, align: NSTextAlignment = .center) {
    let paragraph = NSMutableParagraphStyle()
    paragraph.alignment = align
    paragraph.lineBreakMode = .byWordWrapping
    let attributes: [NSAttributedString.Key: Any] = [
        .font: font(size),
        .foregroundColor: color,
        .paragraphStyle: paragraph,
    ]
    let string = NSAttributedString(string: text, attributes: attributes)
    let bounds = string.boundingRect(
        with: NSSize(width: width, height: 1000),
        options: [.usesLineFragmentOrigin, .usesFontLeading]
    )
    let rect = NSRect(
        x: x,
        y: CGFloat(canvasH) - top - bounds.height,
        width: width,
        height: bounds.height + 6
    )
    string.draw(with: rect, options: [.usesLineFragmentOrigin, .usesFontLeading])
}

func drawApple(cx: CGFloat, centerTop: CGFloat, radius r: CGFloat) {
    let cy = CGFloat(canvasH) - centerTop
    appleDark.setFill()
    NSBezierPath(ovalIn: NSRect(x: cx - r, y: cy - r - 4, width: r * 2, height: r * 2)).fill()
    appleRed.setFill()
    NSBezierPath(ovalIn: NSRect(x: cx - r, y: cy - r, width: r * 2, height: r * 2)).fill()

    NSColor.white.withAlphaComponent(0.16).setFill()
    NSBezierPath(ovalIn: NSRect(
        x: cx - r * 0.65,
        y: cy + r * 0.05,
        width: r * 0.55,
        height: r * 0.42
    )).fill()

    stem.setStroke()
    let stemPath = NSBezierPath()
    stemPath.lineWidth = max(4, r * 0.13)
    stemPath.lineCapStyle = .round
    stemPath.move(to: NSPoint(x: cx, y: cy + r * 0.82))
    stemPath.line(to: NSPoint(x: cx + r * 0.10, y: cy + r * 1.08))
    stemPath.stroke()

    leaf.setFill()
    let leafRect = NSRect(x: cx + r * 0.06, y: cy + r * 0.94, width: r * 0.72, height: r * 0.32)
    let leafPath = NSBezierPath(ovalIn: leafRect)
    leafPath.fill()
}

func drawFlower(cx: CGFloat, centerTop: CGFloat, radius r: CGFloat) {
    let cy = CGFloat(canvasH) - centerTop
    leaf.setStroke()
    let stemPath = NSBezierPath()
    stemPath.lineWidth = max(4, r * 0.08)
    stemPath.lineCapStyle = .round
    stemPath.move(to: NSPoint(x: cx, y: cy - r * 1.4))
    stemPath.line(to: NSPoint(x: cx, y: cy + r * 0.6))
    stemPath.stroke()

    leaf.setFill()
    NSBezierPath(ovalIn: NSRect(x: cx + r * 0.05, y: cy - r * 0.85, width: r * 0.55, height: r * 0.24)).fill()
    NSBezierPath(ovalIn: NSRect(x: cx - r * 0.55, y: cy - r * 0.55, width: r * 0.55, height: r * 0.24)).fill()

    let petals: [(CGFloat, CGFloat)] = [(0, -1), (1.05, -0.25), (0.65, 1.0), (-0.65, 1.0), (-1.05, -0.25)]
    for (dx, dy) in petals {
        flowerRose.setFill()
        NSBezierPath(ovalIn: NSRect(
            x: cx + dx * r * 0.58 - r * 0.38,
            y: cy + dy * r * 0.58 - r * 0.38,
            width: r * 0.76,
            height: r * 0.76
        )).fill()
    }
    flowerCenter.setFill()
    NSBezierPath(ovalIn: NSRect(x: cx - r * 0.26, y: cy - r * 0.26, width: r * 0.52, height: r * 0.52)).fill()
}

func drawCaption(_ text: String) {
    fillRounded(
        topRect(150, 596, 980, 88),
        radius: 28,
        color: NSColor.white.withAlphaComponent(0.94),
        stroke: outline,
        lineWidth: 2
    )
    drawText(text, size: 38, color: ink, top: 620, x: 170, width: 940)
}

func drawTitle(_ title: String, subtitle: String) {
    drawText(title, size: 82, color: ink, top: 138, x: 0, width: CGFloat(canvasW))
    drawText(subtitle, size: 50, color: muted, top: 248, x: 0, width: CGFloat(canvasW))
}

func drawEndCard() {
    drawFlower(cx: 640, centerTop: 300, radius: 90)
    drawText("The end.", size: 76, color: ink, top: 420, x: 0, width: CGFloat(canvasW))
    drawText("Well done.", size: 42, color: muted, top: 520, x: 0, width: CGFloat(canvasW))
}

func sceneAlpha(_ t: Double, _ start: Double) -> Double {
    min(1.0, max(0.0, (t - start) / 0.35))
}

func renderCountFrame(_ t: Double) {
    fillRounded(NSRect(x: 0, y: 0, width: canvasW, height: canvasH), radius: 0, color: cream)

    var fadeStart = 0.0
    if t < 3 {
        drawTitle("Count with me", subtitle: "Apples")
        drawApple(cx: 560, centerTop: 470, radius: 52)
        drawApple(cx: 660, centerTop: 470, radius: 52)
        drawApple(cx: 760, centerTop: 470, radius: 52)
    } else if t < 8.5 {
        fadeStart = 3
        drawApplesLeft(count: 1)
        drawNumeral("1", word: "one")
        drawCaption("One apple.")
    } else if t < 14 {
        fadeStart = 8.5
        drawApplesLeft(count: 2)
        drawNumeral("2", word: "two")
        drawCaption("Two apples.")
    } else if t < 19.5 {
        fadeStart = 14
        drawApplesLeft(count: 3)
        drawNumeral("3", word: "three")
        drawCaption("Three apples.")
    } else if t < 23.5 {
        fadeStart = 19.5
        drawApplesLeft(count: 3)
        drawNumeral("3", word: "three")
        drawCaption("Three apples in all.")
    } else {
        fadeStart = 23.5
        drawEndCard()
    }

    let alpha = sceneAlpha(t, fadeStart)
    if alpha < 1 {
        cream.withAlphaComponent(1 - alpha).setFill()
        NSRect(x: 0, y: 0, width: canvasW, height: canvasH).fill()
    }
}

func drawApplesLeft(count: Int) {
    let r: CGFloat = 86
    let spacing: CGFloat = 28
    let total = CGFloat(count) * r * 2 + CGFloat(count - 1) * spacing
    let startX = 480 - total / 2 + r
    for index in 0..<count {
        drawApple(cx: startX + CGFloat(index) * (r * 2 + spacing), centerTop: 350, radius: r)
    }
}

func drawNumeral(_ numeral: String, word: String) {
    drawText(numeral, size: 290, color: green, top: 138, x: 840, width: 320)
    drawText(word, size: 62, color: ink, top: 460, x: 820, width: 360)
}

func drawGroup(top: CGFloat, count: Int, highlighted: Bool, label: String?) {
    let height: CGFloat = 170
    fillRounded(
        topRect(140, top, 1000, height),
        radius: 30,
        color: highlighted ? NSColor(calibratedRed: 0.863, green: 0.910, blue: 0.867, alpha: 1) : panel,
        stroke: highlighted ? green : outline,
        lineWidth: highlighted ? 5 : 2
    )

    let r: CGFloat = 42
    let spacing: CGFloat = 16
    let startX: CGFloat = 210 + r
    let centerTop = top + height / 2
    for index in 0..<count {
        drawApple(cx: startX + CGFloat(index) * (r * 2 + spacing), centerTop: centerTop, radius: r)
    }

    if highlighted, let label {
        drawText(label, size: 42, color: green, top: top + 62, x: 760, width: 340, align: .right)
    }
}

func renderCompareFrame(_ t: Double) {
    fillRounded(NSRect(x: 0, y: 0, width: canvasW, height: canvasH), radius: 0, color: cream)

    var fadeStart = 0.0
    if t < 3 {
        drawTitle("More and less", subtitle: "Which group has more?")
        drawApple(cx: 560, centerTop: 500, radius: 42)
        drawApple(cx: 660, centerTop: 500, radius: 42)
        drawApple(cx: 760, centerTop: 500, radius: 42)
        drawApple(cx: 860, centerTop: 500, radius: 42)
    } else if t < 9 {
        fadeStart = 3
        drawGroup(top: 118, count: 3, highlighted: false, label: nil)
        drawGroup(top: 318, count: 1, highlighted: false, label: nil)
        drawCaption("Which group has more?")
    } else if t < 15 {
        fadeStart = 9
        drawGroup(top: 118, count: 3, highlighted: true, label: "more")
        drawGroup(top: 318, count: 1, highlighted: false, label: nil)
        drawCaption("Three is more than one.")
    } else if t < 21 {
        fadeStart = 15
        drawGroup(top: 118, count: 1, highlighted: false, label: nil)
        drawGroup(top: 318, count: 3, highlighted: true, label: "fewer")
        drawCaption("One is fewer than three.")
    } else if t < 26.5 {
        fadeStart = 21
        drawGroup(top: 118, count: 4, highlighted: true, label: "more")
        drawGroup(top: 318, count: 2, highlighted: false, label: nil)
        drawCaption("Four is more than two.")
    } else {
        fadeStart = 26.5
        drawEndCard()
    }

    let alpha = sceneAlpha(t, fadeStart)
    if alpha < 1 {
        cream.withAlphaComponent(1 - alpha).setFill()
        NSRect(x: 0, y: 0, width: canvasW, height: canvasH).fill()
    }
}

func renderVideo(name: String, duration: Double, draw: (Double) -> Void) {
    let framesDir = URL(fileURLWithPath: outputRoot)
        .appendingPathComponent("frames")
        .appendingPathComponent(name)
    try? FileManager.default.removeItem(at: framesDir)
    try! FileManager.default.createDirectory(at: framesDir, withIntermediateDirectories: true)

    let total = Int(duration * Double(fps))
    for frame in 0..<total {
        autoreleasepool {
            guard let rep = NSBitmapImageRep(
                bitmapDataPlanes: nil,
                pixelsWide: canvasW,
                pixelsHigh: canvasH,
                bitsPerSample: 8,
                samplesPerPixel: 4,
                hasAlpha: true,
                isPlanar: false,
                colorSpaceName: .deviceRGB,
                bytesPerRow: 0,
                bitsPerPixel: 0
            ) else {
                fatalError("Could not create bitmap")
            }
            NSGraphicsContext.saveGraphicsState()
            let context = NSGraphicsContext(bitmapImageRep: rep)!
            NSGraphicsContext.current = context
            context.imageInterpolation = .high
            draw(Double(frame) / Double(fps))
            context.flushGraphics()
            NSGraphicsContext.restoreGraphicsState()

            let data = rep.representation(using: .png, properties: [:])!
            let file = framesDir.appendingPathComponent(String(format: "%05d.png", frame))
            try! data.write(to: file)

            if frame % 120 == 0 {
                FileHandle.standardError.write("\(name): frame \(frame)/\(total)\n".data(using: .utf8)!)
            }
        }
    }
}

let args = CommandLine.arguments
guard args.count >= 2 else {
    FileHandle.standardError.write("Usage: render_media.swift <output-dir>\n".data(using: .utf8)!)
    exit(1)
}
let outputRoot = args[1]
try! FileManager.default.createDirectory(
    atPath: outputRoot,
    withIntermediateDirectories: true
)

renderVideo(name: "count_to_3", duration: 26.0) { renderCountFrame($0) }
renderVideo(name: "more_and_less", duration: 28.0) { renderCompareFrame($0) }
FileHandle.standardError.write("Frames complete.\n".data(using: .utf8)!)
