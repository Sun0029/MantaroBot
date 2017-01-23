package net.kodehawa.mantarobot.cmd;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.kodehawa.mantarobot.module.Callback;
import net.kodehawa.mantarobot.module.Category;
import net.kodehawa.mantarobot.module.CommandType;
import net.kodehawa.mantarobot.module.Module;
import net.kodehawa.mantarobot.util.GeneralUtils;
import net.kodehawa.mantarobot.util.StringArrayUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Misc extends Module {

	private CopyOnWriteArrayList<String> facts = new CopyOnWriteArrayList<>();
	private List<String> lyrics = new ArrayList<>();
	private CopyOnWriteArrayList<String> nobleQuotes = new CopyOnWriteArrayList<>();
	private ArrayList<User> users = new ArrayList<>();

	public Misc() {
		super.setCategory(Category.MISC);
		lyrics.add(":mega: Are you ready?");
		lyrics.add("O-oooooooooo AAAAE-A-A-I-A-U-");
		lyrics.add("E-eee-ee-eee AAAAE-A-E-I-E-A-");
		lyrics.add("JO-ooo-oo-oo-oo EEEEO-A-AAA-AAAA");
		lyrics.add("O-oooooooooo AAAAE-A-A-I-A-U-");
		lyrics.add("JO-oooooooooooo AAE-O-A-A-U-U-A-");
		lyrics.add("E-eee-ee-eee AAAAE-A-E-I-E-A-");
		lyrics.add("JO-ooo-oo-oo-oo EEEEO-A-AAA-AAAA");
		lyrics.add("O-oooooooooo AAAAE-A-A-I-A-U-");
		lyrics.add("E-eee-ee-eee AAAAE-A-E-I-E-A-");
		lyrics.add("JO-ooo-oo-oo-oo EEEEO-A-AAA-AAAA-");

		this.registerCommands();
		new StringArrayUtils("facts", facts, false);
		new StringArrayUtils("noble", nobleQuotes, false);
	}

	@Override
	public void registerCommands() {
		super.register("lottery", "Get random amounts of money! Usable every 20m per person.", new Callback() {
			@Override
			public CommandType commandType() {
				return CommandType.USER;
			}

			@Override
			public void onCommand(String[] args, String content, GuildMessageReceivedEvent event) {
				User author = event.getAuthor();
				if (!users.contains(author)) {
					Random r1 = new Random();
					int lottery = r1.nextInt(5000);
					event.getChannel().sendMessage(":speech_balloon: " + "You won **" + lottery + "USD**, congrats!").queue();
					users.add(author);
				} else {
					event.getChannel().sendMessage(":speech_balloon: " + "Try again in later! (10 minutes since you ran the command)").queue();
				}

				if (users.contains(author)) {
					TimerTask timerTask = new TimerTask() {
						public void run() {
							users.remove(author);
							this.cancel();
						}
					};
					Timer timer = new Timer();
					timer.scheduleAtFixedRate(timerTask, 600000, 1);
				}
			}

			@Override
			public String help() {
				return "Retrieves a random amount of money. Usable every 20 minutes.";
			}

		});

		super.register("randomfact", "Displays a random fact.", new Callback() {
			@Override
			public void onCommand(String[] args, String content, GuildMessageReceivedEvent event) {
				Random rand = new Random();
				int factrand = rand.nextInt(facts.size());
				event.getChannel().sendMessage(":speech_balloon: " + facts.get(factrand)).queue();
			}

			@Override
			public String help() {
				return getDescription("randomfact")[0];
			}

			@Override
			public CommandType commandType() {
				return CommandType.USER;
			}
		});
		super.register("misc", "Misc funny commands", new Callback() {
			@Override
			public void onCommand(String[] args, String content, GuildMessageReceivedEvent event) {
				String mentioned = "";
				try {
					mentioned = event.getMessage().getMentionedUsers().get(0).getAsMention();
				} catch (IndexOutOfBoundsException ignored) {
				}
				TextChannel channel = event.getChannel();
				Random rand = new Random();
				String noArgs = content.split(" ")[0];
				switch (noArgs) {
					case "rob":
						Random r = new Random();
						int woah = r.nextInt(1200);
						channel.sendMessage(":speech_balloon: " + "You robbed **" + woah + "USD** from " + mentioned).queue();
						break;
					case "reverse":
						String stringToReverse = content.replace("reverse ", "");
						String reversed = new StringBuilder(stringToReverse).reverse().toString();
						channel.sendMessage(reversed).queue();
						break;
					case "bp":
						StringBuilder finalMessage = new StringBuilder();
						for (String help : lyrics) {
							finalMessage.append(help).append("\n\n");
						}
						channel.sendMessage(finalMessage.toString()).queue();
						break;
					case "rndcolor":
						String s = String.format(":speech_balloon: Your random color is %s", randomColor());
						channel.sendMessage(s).queue();
						break;
					case "noble":
						int nobleQuote = rand.nextInt(nobleQuotes.size());
						channel.sendMessage(":speech_balloon: " + nobleQuotes.get(nobleQuote) + " -Noble").queue();
						break;
					default:
						channel.sendMessage(help()).queue();
						break;
				}
			}

			@Override
			public String help() {
				return "Miscellaneous funny/useful commands. Ranges from funny commands and random colors to bot hardware information\n"
					+ "Usage:\n"
					+ "~>misc rob [@user]: Rob random amount of money from a user.\n"
					+ "~>misc reverse [sentence]: Reverses any given sentence.\n"
					+ "~>misc bp: Brain power lyrics.\n"
					+ "~>misc noble: Random Lost Pause quote.\n"
					+ "~>misc rndcolor: Gives you a random hex color.\n"
					+ "Parameter explanation:\n"
					+ "[sentence]: A sentence to reverse."
					+ "[@user]: A user to mention.";
			}

			@Override
			public CommandType commandType() {
				return CommandType.USER;
			}
		});
		super.register("8ball", "Retrieves information from 8ball", new Callback() {
			@Override
			public void onCommand(String[] args, String content, GuildMessageReceivedEvent event) {
				if (content.isEmpty()) {
					String textEncoded = "";
					String url2;

					try {
						textEncoded = URLEncoder.encode(content, "UTF-8");
					} catch (UnsupportedEncodingException ignored) {
					} //Shouldn't fail.

					String URL = String.format("https://8ball.delegator.com/magic/JSON/%1s", textEncoded);
					url2 = GeneralUtils.instance().restyGetObjectFromUrl(URL, event);

					JSONObject jObject = new JSONObject(url2);
					JSONObject data = jObject.getJSONObject("magic");

					event.getChannel().sendMessage(":speech_balloon: " + data.getString("answer") + ".").queue();
				} else {
					event.getChannel().sendMessage(help()).queue();
				}
			}

			@Override
			public String help() {
				return "Retrieves an answer from 8Ball. Requires a sentence.\n"
					+ "~>8ball [question]. Retrieves an answer from 8ball based on the question provided.";
			}

			@Override
			public CommandType commandType() {
				return CommandType.USER;
			}
		});
		super.register("urban", "Retrieves information from urban dictionary", new Callback() {
			@Override
			public void onCommand(String[] args, String content, GuildMessageReceivedEvent event) {
				//First split is definition, second one is number. I would use space but we need the ability to fetch with spaces too.
				String beheadedSplit[] = content.split("->");
				EmbedBuilder embed = new EmbedBuilder();

				if (!content.isEmpty()) {
					ArrayList<String> definitions = new ArrayList<>(); //Will use later to store definitions.
					ArrayList<String> thumbsup = new ArrayList<>();
					ArrayList<String> thumbsdown = new ArrayList<>(); //Will use later to store definitions.
					ArrayList<String> urls = new ArrayList<>(); //Will use later to store definitions.
					long start = System.currentTimeMillis();
					String url = null;
					try {
						url = "http://api.urbandictionary.com/v0/define?term=" + URLEncoder.encode(beheadedSplit[0], "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					String json = GeneralUtils.instance().restyGetObjectFromUrl(url, event);
					JSONObject jObject = new JSONObject(json);
					JSONArray data = jObject.getJSONArray("list");
					for (int i = 0; i < data.length(); i++) { //Loop though the JSON
						JSONObject entry = data.getJSONObject(i);
						//Get the definition from the JSON.
						definitions.add(entry.getString("definition"));
						thumbsup.add(entry.get("thumbs_up").toString()); //int -> String
						thumbsdown.add(entry.get("thumbs_down").toString()); //int -> String
						urls.add(entry.getString("permalink"));
					}
					long end = System.currentTimeMillis() - start;
					switch (beheadedSplit.length) {
						case 1:
							embed.setTitle("Urban Dictionary definition for " + content)
								.setDescription("Main definition.")
								.setThumbnail("https://everythingfat.files.wordpress.com/2013/01/ud-logo.jpg")
								.setUrl(urls.get(0))
								.setColor(Color.GREEN)
								.addField("Definition", definitions.get(0), false)
								.addField("Thumbs up", thumbsup.get(0), true)
								.addField("Thumbs down", thumbsdown.get(0), true)
								.setFooter("Information by Urban Dictionary (Process time: " + end + "ms)", null);
							event.getChannel().sendMessage(embed.build()).queue();
							break;
						case 2:
							int defn = Integer.parseInt(beheadedSplit[1]) - 1;
							String defns = String.valueOf(defn + 1);
							embed.setTitle("Urban Dictionary definition for " + beheadedSplit[0])
								.setThumbnail("https://everythingfat.files.wordpress.com/2013/01/ud-logo.jpg")
								.setDescription("Definition " + defns)
								.setColor(Color.PINK)
								.setUrl(urls.get(defn))
								.addField("Definition", definitions.get(defn), false)
								.addField("Thumbs up", thumbsup.get(defn), true)
								.addField("Thumbs down", thumbsdown.get(defn), true)
								.setFooter("Information by Urban Dictionary", null);
							event.getChannel().sendMessage(embed.build()).queue();
							break;
						default:
							event.getChannel().sendMessage(help()).queue();
							break;
					}
				}
			}

			@Override
			public String help() {
				return "Retrieves definitions from **Urban Dictionary**.\n"
					+ "Usage: \n"
					+ "~>urban [term]->[number]: Gets a definition based on parameters.\n"
					+ "Parameter description:\n"
					+ "[term]: The term you want to look up the urban definition for.\n"
					+ "[number]: **OPTIONAL** Parameter defined with the modifier '->' after the term. You don't need to use it.\n"
					+ "For example putting 2 will fetch the second result on Urban Dictionary";
			}

			@Override
			public CommandType commandType() {
				return CommandType.USER;
			}
		});
	}

	/**
	 * @return a random hex color.
	 */
	private String randomColor() {
		String[] letters = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		String color = "#";
		for (int i = 0; i < 6; i++) {
			color += letters[(int) Math.floor(Math.random() * 16)];
		}
		return color;
	}
}
